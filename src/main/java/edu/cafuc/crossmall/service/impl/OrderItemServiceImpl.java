package edu.cafuc.crossmall.service.impl;

import edu.cafuc.crossmall.mapper.OrderItemMapper;
import edu.cafuc.crossmall.pojo.OrderItem;
import edu.cafuc.crossmall.service.OrderItemService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderItemServiceImpl implements OrderItemService {

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Override
    public Integer insertOrderItem(Long orderId, Long productId, String productName, BigDecimal price, Integer quantity) {
        return orderItemMapper.insertOrderItem(orderId, productId, productName, price, quantity);
    }

    @Override
    public Integer deleteOrderItemById(Long orderId) {
        return orderItemMapper.deleteOrderItemById(orderId);
    }

    @Override
    //查询某订单下的所有明细 卖家 和卖家
    public List<OrderItem> selectOrderItemForSeller(Long orderId,Long userId){
        return orderItemMapper.selectOrderItemForSeller(orderId,userId);
    }

    @Override
    public List<OrderItem> selectOrderItemByOrderId(Long orderId) {
        return orderItemMapper.selectOrderItemByOrderId(orderId);
    }
}
