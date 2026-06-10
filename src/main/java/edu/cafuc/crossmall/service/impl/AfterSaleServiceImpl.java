package edu.cafuc.crossmall.service.impl;

import edu.cafuc.crossmall.exception.BusinessException;
import edu.cafuc.crossmall.mapper.AfterSaleMapper;
import edu.cafuc.crossmall.mapper.OrderMapper;
import edu.cafuc.crossmall.mapper.PaymentMapper;
import edu.cafuc.crossmall.pojo.AfterSale;
import edu.cafuc.crossmall.pojo.Order;
import edu.cafuc.crossmall.pojo.OrderItem;
import edu.cafuc.crossmall.pojo.Payment;
import edu.cafuc.crossmall.pojo.vo.AfterSaleVO;
import edu.cafuc.crossmall.mapper.OrderItemMapper;
import edu.cafuc.crossmall.service.AfterSaleService;
import edu.cafuc.crossmall.service.OrderService;
import edu.cafuc.crossmall.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AfterSaleServiceImpl implements AfterSaleService {

    @Autowired
    private AfterSaleMapper afterSaleMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private PaymentMapper paymentMapper;
    @Autowired
    private ProductService productService;
    @Autowired
    private OrderService orderService;

    @Override
    //申请售后
    public Integer apply(Long userId, String orderNo, Integer type, String reason) {
        if (orderNo == null || orderNo.isBlank()) {
            throw new BusinessException("订单号不能为空");
        }
        if (type == null || type < 1 || type > 4) {
            throw new BusinessException("售后类型无效");
        }
        if (reason == null || reason.isBlank()) {
            throw new BusinessException("请填写申请原因");
        }

        Order order = orderMapper.selectOrderByOrderNo(orderNo, userId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        Integer orderStatus = order.getStatus();
        if (orderStatus == null || orderStatus == 0) {
            throw new BusinessException("待支付订单请直接删除，无需售后");
        }
        if (orderStatus == 4) {
            throw new BusinessException("已取消订单无法申请售后");
        }

        validateTypeForOrderStatus(type, orderStatus);
        validateNotRefunded(order.getId(), type);

        if (afterSaleMapper.selectActiveByOrderId(order.getId()) != null) {
            throw new BusinessException("该订单已有进行中的售后申请");
        }

        AfterSale afterSale = new AfterSale();
        afterSale.setOrderId(order.getId());
        afterSale.setUserId(userId);
        afterSale.setType(type);
        afterSale.setReason(reason.trim());
        afterSale.setStatus(0);
        return afterSaleMapper.insertAfterSale(afterSale);
    }

    @Override
    //买家 申请的售后列表
    public List<AfterSaleVO> listByUser(Long userId) {
        return afterSaleMapper.selectByUserId(userId);
    }

    @Override
    //该订单的 售后列表
    public List<AfterSaleVO> listByOrderNo(String orderNo, Long userId) {
        Order order = orderMapper.selectOrderByOrderNo(orderNo, userId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        return afterSaleMapper.selectByOrderId(order.getId());
    }

    @Override
    //卖家 申请的售后列表
    public List<AfterSaleVO> listForSeller(Long sellerUserId) {
        return afterSaleMapper.selectListForSeller(sellerUserId);
    }

    @Override
    @Transactional
    //售后处理
    public Integer handle(Long id, Long sellerUserId, Integer status, String reply, String company) {
        if (id == null) {
            throw new BusinessException("售后单不存在");
        }
        if (status == null || (status != 1 && status != 2 && status != 3)) {
            throw new BusinessException("状态无效");
        }

        AfterSale afterSale = afterSaleMapper.selectById(id);
        if (afterSale == null) {
            throw new BusinessException("售后单不存在");
        }

        Order order = orderMapper.selectOrderForSellerByOrderId(afterSale.getOrderId(), sellerUserId);
        if (order == null) {
            throw new BusinessException("无权处理该售后");
        }

        Integer currentStatus = afterSale.getStatus();
        if (currentStatus == null || currentStatus == 2 || currentStatus == 3) {
            throw new BusinessException("该售后已结案，无法操作");
        }

        Integer expectedStatus;
        if (status == 1) {
            if (currentStatus != 0) {
                throw new BusinessException("仅待处理申请可受理");
            }
            expectedStatus = 0;
        } else if (status == 2) {
            if (currentStatus != 1) {
                throw new BusinessException("请先受理后再完成");
            }
            expectedStatus = 1;
        } else {
            if (currentStatus != 0 && currentStatus != 1) {
                throw new BusinessException("当前状态无法拒绝");
            }
            expectedStatus = currentStatus;
        }

        String replyText = reply != null ? reply.trim() : "";
        if ((status == 2 || status == 3) && replyText.isEmpty()) {
            throw new BusinessException("请填写回复内容");
        }

        if (status == 2) {
            applyCompletionSideEffects(afterSale.getType(), order, sellerUserId, company);
        }

        Integer rows = afterSaleMapper.updateHandle(id, status, replyText.isEmpty() ? null : replyText, expectedStatus);
        if (rows == null || rows == 0) {
            throw new BusinessException("更新失败，请刷新后重试");
        }
        return rows;
    }

    //还没发货 无法售后
    private void validateTypeForOrderStatus(Integer type, Integer orderStatus) {
        if (orderStatus == 1) {
            if (type == 1 || type == 2) {
                throw new BusinessException("未发货订单仅可申请投诉或仅退款");
            }
            return;
        }
        if (orderStatus == 2 || orderStatus == 3) {
            return;
        }
        throw new BusinessException("当前订单状态无法申请售后");
    }

    //退货和仅退款不能再 申请售后
    private void validateNotRefunded(Long orderId, Integer type) {
        if (type != 1 && type != 4) {
            return;
        }
        Payment payment = paymentMapper.selectByOrderId(orderId);
        if (payment != null && payment.getStatus() != null && payment.getStatus() == 2) {
            throw new BusinessException("该订单已退款，无法再次申请");
        }
    }

    //售后逻辑实现
    private void applyCompletionSideEffects(Integer type, Order order, Long sellerUserId, String company) {
        if (type == null) {
            throw new BusinessException("售后类型无效");
        }
        switch (type) {
            //更新库存，退款
            case 1 -> {
                restoreStock(order.getId());
                simulateRefund(order.getId());
            }
            //换货 - 更新物流
            case 2 -> {
                if (company == null || company.isBlank()) {
                    throw new BusinessException("换货补发请填写物流公司");
                }
                orderService.reshipForExchange(order.getOrderNo(), sellerUserId, company);
            }
            case 3 -> {
                // 投诉：仅更新售后单
            }
            //退款
            case 4 -> simulateRefund(order.getId());
            default -> throw new BusinessException("售后类型无效");
        }
    }

    //更新库存
    private void restoreStock(Long orderId) {
        List<OrderItem> orderItems = orderItemMapper.selectOrderItemByOrderId(orderId);
        for (OrderItem orderItem : orderItems) {
            Integer quantity = orderItem.getQuantity();
            Long productId = orderItem.getProductId();
            Integer rows = productService.addStock(productId, quantity);
            if (rows == null || rows == 0) {
                throw new BusinessException("回库存失败，请重试");
            }
        }
    }

    //仅退款
    private void simulateRefund(Long orderId) {
        Payment payment = paymentMapper.selectByOrderId(orderId);
        if (payment == null || payment.getStatus() == null || payment.getStatus() != 1) {
            throw new BusinessException("无可退款项");
        }
        Integer rows = paymentMapper.updatePaymentRefund(orderId);
        if (rows == null || rows == 0) {
            throw new BusinessException("退款失败，请重试");
        }
    }
}
