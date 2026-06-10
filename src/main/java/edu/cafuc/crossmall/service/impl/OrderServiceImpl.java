package edu.cafuc.crossmall.service.impl;

import edu.cafuc.crossmall.mapper.*;
import edu.cafuc.crossmall.pojo.Cart;
import edu.cafuc.crossmall.pojo.Logistics;
import edu.cafuc.crossmall.pojo.LogisticsTrack;
import edu.cafuc.crossmall.pojo.Order;
import edu.cafuc.crossmall.pojo.OrderItem;
import edu.cafuc.crossmall.pojo.Payment;
import edu.cafuc.crossmall.pojo.Product;
import edu.cafuc.crossmall.pojo.vo.OrderVO;
import edu.cafuc.crossmall.pojo.vo.ProductVO;
import edu.cafuc.crossmall.service.OrderService;
import edu.cafuc.crossmall.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;


@Service
public class OrderServiceImpl implements OrderService {
    private static final String ORDER_NO_PREFIX = "CM";
    private static final String TRACKING_NO_PREFIX = "TN";
    private static final String PAY_NO_PREFIX = "TN";
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private LogisticsMapper logisticsMapper;
    @Autowired
    private LogisticsTrackMapper logisticsTrackMapper;
    @Autowired
    private PaymentMapper paymentMapper;
    @Autowired
    private ProductService productService;

    @Override
    @Transactional
    public Order addOrder(Long userId,Integer payType,Integer status,String address,String receiverName,String receiverPhone) {
       for (int i = 0; i < 3; i++) {
            Order order = new Order();
            order.setOrderNo(generateOrderNo());
            order.setUserId(userId);
            order.setPayType(payType);
            order.setStatus(status);
            order.setAddress(address);
            order.setReceiverName(receiverName);
            order.setReceiverPhone(receiverPhone);
            try {
                List<Cart> carts = cartMapper.selectCartByUserId(userId);
                if (carts == null || carts.isEmpty()) {
                    throw new RuntimeException("购物车没有货物");
                }
                //amount计算
                BigDecimal totalAmount = BigDecimal.ZERO;
                for (Cart cart : carts) {
                    Long productId = cart.getProductId();
                    ProductVO productvo = productMapper.selectProductById(productId);
                    if (productvo == null) {
                        throw new RuntimeException("商品已经下架，请重试");
                    }
                    if(productService.selectStockById(productId) < cart.getQuantity()){
                        throw new RuntimeException("库存不够，请重试");
                    }
                    BigDecimal price = productvo.getPrice();
                    price = price.multiply(new BigDecimal(cart.getQuantity()));
                    totalAmount = totalAmount.add(price);
                }
                order.setTotalAmount(totalAmount);
                //生成order
                orderMapper.insertOrder(order);
                /** 获取购物车信息，product细节详情 导入到order_item */
                for(Cart cart : carts){

                    //从购物车取出product得到详情
                    Long productId = cart.getProductId();
                    Integer quantity = cart.getQuantity();
                    ProductVO productvo = productMapper.selectProductById(productId);
                    Long orderId = order.getId();
                    String productName = productvo.getProductName();
                    BigDecimal price = productvo.getPrice();

                    //导入到order_item
                    orderItemMapper.insertOrderItem(orderId,productId,productName,price,quantity);

                    //更新库存
                    Integer rows = productService.deductStock(productId, quantity);
                    if(rows == 0){
                        throw new RuntimeException("更新库存失败失败，请重试");
                    }
                }
                //清空购物车
                cartMapper.deleteCartByUserId(userId);
                return order;
            } catch (org.springframework.dao.DuplicateKeyException e) {
                // 订单号冲突，重新生成再试
            }
        }
        throw new RuntimeException("生成订单号失败，请重试");
    }

    private String generateOrderNo() {
        String datePart = LocalDate.now().format(DATE_FMT);
        String prefix = ORDER_NO_PREFIX + datePart;  // CM20250609
        String maxOrderNo = orderMapper.selectMaxOrderNoByPrefix(prefix);
        int seq = 1;
        if (maxOrderNo != null && maxOrderNo.startsWith(prefix) && maxOrderNo.length() == prefix.length() + 4) {
            seq = Integer.parseInt(maxOrderNo.substring(prefix.length())) + 1;
        }
        if (seq > 9999) {
            throw new RuntimeException("当日订单号已用尽，请明天再试");
        }
        return prefix + String.format("%04d", seq);
    }

    /** 更新订单（收货信息） */
    @Override
    public Integer updateReceiverInfo(String orderNo,Long userId,Integer payType,String address,String receiverName,String receiverPhone){
        return orderMapper.updateReceiverInfo(orderNo,userId,payType,address,receiverName,receiverPhone);
    }

    /** 更新状态（删除和确认） */
    @Override
    @Transactional
    public Integer confirmReceipt(String orderNo, Long userId) {
        Order order = orderMapper.selectOrderByOrderNo(orderNo, userId);
        if (order == null) {
            return 0;
        }
        Integer rows = orderMapper.updateOrderStatus(orderNo, userId, 3, 2);
        if (rows > 0) {
            Logistics logistics = logisticsMapper.selectByOrderId(order.getId());
            if (logistics != null) {
                logistics.setStatus(2);
                logisticsMapper.updateLogistics(logistics);
            }
        }
        return rows;
    }

    //前端根据订单号和用户id删除 订单和订单详情 并更新库存， 查询订单详情需要订单id
    @Override
    public Integer deleteOrderByOrderNo(String orderNo,Long userId) {
        //TODO:更新库存
        //找到order 根据orderNo
        Order order = orderMapper.selectOrderByOrderNo(orderNo, userId);
        //返回需要的参数orderId
        Long orderId = order.getId();
        //找到订单详情 根据orderId
        List<OrderItem> orderItems = orderItemMapper.selectOrderItemByOrderId(orderId);
        //更新库存
        for (OrderItem orderItem : orderItems) {
            Integer quantity = orderItem.getQuantity();
            Long productId = orderItem.getProductId();
            Integer rows = productService.addStock(productId, quantity);
            if(rows == 0){
                throw new RuntimeException("更新库存失败，请重试");
            }
        }
        //删除订单详情
        Integer rows = orderItemMapper.deleteOrderItemById(orderId);
        if(rows == 0){
            throw new RuntimeException("删除订单详情失败，请重试");
        }
        return orderMapper.deleteOrderByOrderNo(orderNo,userId);
    }
//查询
    //买家订单列表
    @Override
    public List<Order> selectOrderByUserId(Long userId) {
        return orderMapper.selectOrderByUserId(userId);
    }

    //买家订单
    @Override
    public OrderVO selectOrderByOrderNo(String orderNo, Long userId) {
        Order order = orderMapper.selectOrderByOrderNo(orderNo, userId);
        if (order == null) {
            return null;
        }
        OrderVO vo = new OrderVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setUserId(order.getUserId());
        vo.setTotalAmount(order.getTotalAmount());
        vo.setPayType(order.getPayType());
        vo.setStatus(order.getStatus());
        vo.setAddress(order.getAddress());
        vo.setReceiverName(order.getReceiverName());
        vo.setReceiverPhone(order.getReceiverPhone());
        vo.setCreateTime(order.getCreateTime());
        vo.setUpdateTime(order.getUpdateTime());
        vo.setItems(orderItemMapper.selectOrderItemByOrderId(order.getId()));
        //物流信息
        Logistics logistics = logisticsMapper.selectByOrderId(order.getId());
        vo.setLogistics(logistics);
        if (logistics != null) {
            List<LogisticsTrack> tracks = logisticsTrackMapper.selectByLogisticsId(logistics.getId());
            vo.setTracks(tracks);
        } else {
            vo.setTracks(Collections.emptyList());
        }
        return vo;
    }
    //卖家订单列表
    @Override
    public List<Order> selectOrderListForSeller(Long userId){
        return orderMapper.selectOrderListForSeller(userId);
    }

    //卖家订单
    @Override
    public OrderVO selectOrderForSeller(String orderNo,Long userId){
        Order order = orderMapper.selectOrderForSeller(orderNo, userId);
        if (order == null) {
            return null;
        }
        OrderVO vo = new OrderVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setUserId(order.getUserId());
        //amount计算-卖家
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = orderItemMapper.selectOrderItemForSeller(order.getId(),userId);
        for (OrderItem orderItem : orderItems) {
            BigDecimal price = orderItem.getPrice();
            price = price.multiply(new BigDecimal(orderItem.getQuantity()));
            totalAmount = totalAmount.add(price);
        }
        vo.setTotalAmount(totalAmount);
        vo.setPayType(order.getPayType());
        vo.setStatus(order.getStatus());
        vo.setAddress(order.getAddress());
        vo.setReceiverName(order.getReceiverName());
        vo.setReceiverPhone(order.getReceiverPhone());
        vo.setCreateTime(order.getCreateTime());
        vo.setUpdateTime(order.getUpdateTime());
        vo.setItems(orderItems);
        //物流信息
        Logistics logistics = logisticsMapper.selectByOrderId(order.getId());
        vo.setLogistics(logistics);
        if (logistics != null) {
            List<LogisticsTrack> tracks = logisticsTrackMapper.selectByLogisticsId(logistics.getId());
            vo.setTracks(tracks);
        } else {
            vo.setTracks(Collections.emptyList());
        }
        return vo;
    }

    @Override
    @Transactional
    public Integer payOrder(String orderNo, Long userId) {
        Order order = orderMapper.selectOrderByOrderNo(orderNo, userId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getStatus() != 0) {
            throw new RuntimeException("订单状态不允许支付");
        }
        Payment existing = paymentMapper.selectByOrderId(order.getId());
        if (existing != null && Integer.valueOf(1).equals(existing.getStatus())) {
            throw new RuntimeException("订单已支付");
        }

        Payment payment = new Payment();
        payment.setOrderId(order.getId());
        payment.setPayNo(generatePayNo(order.getPayType()));
        payment.setPayType(order.getPayType());
        payment.setAmount(order.getTotalAmount());
        payment.setStatus(1);
        payment.setPayTime(LocalDateTime.now());

        Integer paymentRows;
        if (existing != null && Integer.valueOf(0).equals(existing.getStatus())) {
            paymentRows = paymentMapper.updatePaymentSuccess(payment);
        } else {
            paymentRows = paymentMapper.insertPayment(payment);
        }
        if (paymentRows == null || paymentRows <= 0) {
            throw new RuntimeException("支付记录写入失败");
        }

        return orderMapper.updateOrderStatus(orderNo, userId, 1, 0);
    }

    @Override
    @Transactional
    public Logistics shipOrder(String orderNo, Long sellerUserId, String company,
                               LocalDateTime estimatedArrival, String content) {
        Order order = requireSellerOrder(orderNo, sellerUserId);
        if (order.getStatus() != 1) {
            throw new RuntimeException("仅已支付订单可发货");
        }
        if (logisticsMapper.selectByOrderId(order.getId()) != null) {
            throw new RuntimeException("该订单已发货，请使用更新物流接口");
        }
        if (company == null || company.isBlank()) {
            throw new RuntimeException("请填写物流公司");
        }

        Logistics logistics = new Logistics();
        logistics.setOrderId(order.getId());
        logistics.setCompany(company.trim());
        logistics.setTrackingNo(generateTrackingNo(company));
        logistics.setStatus(1);
        logistics.setEstimatedArrival(estimatedArrival);
        logisticsMapper.insertLogistics(logistics);

        String trackContent = (content != null && !content.isBlank())
                ? content.trim()
                : "商家已发货，运单号 " + logistics.getTrackingNo();
        insertTrack(logistics.getId(), trackContent, LocalDateTime.now());

        Integer rows = orderMapper.updateOrderStatusByOrderNo(orderNo, 2, 1);
        if (rows == 0) {
            throw new RuntimeException("更新订单状态失败");
        }
        return logistics;
    }

    @Override
    public Integer updateLogistics(String orderNo, Long sellerUserId, String company,
                                   LocalDateTime estimatedArrival, Integer status) {
        Order order = requireSellerOrder(orderNo, sellerUserId);
        Logistics logistics = logisticsMapper.selectByOrderId(order.getId());
        if (logistics == null) {
            throw new RuntimeException("该订单尚未发货，请先发货");
        }
        Integer currentStatus = logistics.getStatus();
        if (currentStatus != null && (currentStatus == 2 || currentStatus == 3)) {
            throw new RuntimeException("物流已送达或已签收，无法修改");
        }
        if (status != null) {
            if (status == 0 || status == 2) {
                throw new RuntimeException("物流状态不允许该操作");
            }
            if (status != 1 && status != 3) {
                throw new RuntimeException("物流状态无效");
            }
        }
        if (company != null && !company.isBlank()) {
            logistics.setCompany(company.trim());
        }
        if (estimatedArrival != null) {
            logistics.setEstimatedArrival(estimatedArrival);
        }
        boolean markedDelivered = false;
        if (status != null) {
            markedDelivered = status == 3 && !Integer.valueOf(3).equals(currentStatus);
            logistics.setStatus(status);
        }
        Integer rows = logisticsMapper.updateLogistics(logistics);
        if (rows > 0 && markedDelivered) {
            insertTrack(logistics.getId(), "包裹已送达，等待买家确认收货", LocalDateTime.now());
        }
        return rows;
    }

    @Override
    public Integer addLogisticsTrack(String orderNo, Long sellerUserId, String content, LocalDateTime trackTime) {
        Order order = requireSellerOrder(orderNo, sellerUserId);
        Logistics logistics = logisticsMapper.selectByOrderId(order.getId());
        if (logistics == null) {
            throw new RuntimeException("该订单尚未发货，请先发货");
        }
        Integer currentStatus = logistics.getStatus();
        if (currentStatus != null && (currentStatus == 2 || currentStatus == 3)) {
            throw new RuntimeException("物流已送达或已签收，无法追加轨迹");
        }
        if (content == null || content.isBlank()) {
            throw new RuntimeException("请填写轨迹描述");
        }
        LocalDateTime time = trackTime != null ? trackTime : LocalDateTime.now();
        return insertTrack(logistics.getId(), content.trim(), time);
    }

    @Override
    @Transactional
    public Logistics reshipForExchange(String orderNo, Long sellerUserId, String company) {
        Order order = requireSellerOrder(orderNo, sellerUserId);
        Logistics logistics = logisticsMapper.selectByOrderId(order.getId());
        if (logistics == null) {
            throw new RuntimeException("该订单尚未发货，无法换货补发");
        }
        if (company == null || company.isBlank()) {
            throw new RuntimeException("请填写物流公司");
        }

        String trackingNo = generateTrackingNo(company);
        logistics.setCompany(company.trim());
        logistics.setTrackingNo(trackingNo);
        Integer rows = logisticsMapper.updateLogisticsReship(logistics);
        if (rows == null || rows == 0) {
            throw new RuntimeException("换货补发失败，请重试");
        }

        insertTrack(logistics.getId(),
                "【换货补发】商家已发出新货，运单号 " + trackingNo,
                LocalDateTime.now());
        return logisticsMapper.selectByOrderId(order.getId());
    }

    private Order requireSellerOrder(String orderNo, Long sellerUserId) {
        Order order = orderMapper.selectOrderForSeller(orderNo, sellerUserId);
        if (order == null) {
            throw new RuntimeException("订单不存在或无权操作");
        }
        return order;
    }

    private Integer insertTrack(Long logisticsId, String content, LocalDateTime trackTime) {
        LogisticsTrack track = new LogisticsTrack();
        track.setLogisticsId(logisticsId);
        track.setContent(content);
        track.setTrackTime(trackTime);
        return logisticsTrackMapper.insertTrack(track);
    }

    /** 运单号：公司前两字母 + 日期 + 4 位序号，如 DH202506090001；无字母则用 TN */
    private String generateTrackingNo(String company) {
        String letters = company.replaceAll("[^A-Za-z]", "").toUpperCase();
        String prefix = letters.length() >= 2 ? letters.substring(0, 2) : TRACKING_NO_PREFIX;
        String fullPrefix = prefix + LocalDate.now().format(DATE_FMT);
        String maxNo = logisticsMapper.selectMaxTrackingNoByPrefix(fullPrefix);
        return fullPrefix + nextSeq(fullPrefix, maxNo);
    }

    private String nextSeq(String fullPrefix, String maxNo) {
        int seq = 1;
        if (maxNo != null && maxNo.startsWith(fullPrefix) && maxNo.length() == fullPrefix.length() + 4) {
            seq = Integer.parseInt(maxNo.substring(fullPrefix.length())) + 1;
        }
        if (seq > 9999) {
            throw new RuntimeException("当日序号已用尽，请明天再试");
        }
        return String.format("%04d", seq);
    }

    /** payNo：payType 1→ALI、2→WX、3→CC + 日期 + 4 位序号 */
    private String generatePayNo(Integer payType) {
        String prefix = PAY_NO_PREFIX;
        if (payType != null) {
            switch (payType) {
                case 1 -> prefix = "ALI";
                case 2 -> prefix = "WX";
                case 3 -> prefix = "CC";
                default -> prefix = PAY_NO_PREFIX;
            }
        }
        String fullPrefix = prefix + LocalDate.now().format(DATE_FMT);
        String maxNo = paymentMapper.selectMaxPayNoByPrefix(fullPrefix);
        return fullPrefix + nextSeq(fullPrefix, maxNo);
    }


}
