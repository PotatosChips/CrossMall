package edu.cafuc.crossmall.service.impl;

import edu.cafuc.crossmall.mapper.OrderItemMapper;
import edu.cafuc.crossmall.pojo.OrderItem;
import edu.cafuc.crossmall.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderItemServiceImpl implements OrderItemService {

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Override
    public Integer insertOrderItem(OrderItem orderItem) {
        return orderItemMapper.insertOrderItem(orderItem);
    }

    @Override
    public OrderItem selectOrderItemById(Long id) {
        return orderItemMapper.selectOrderItemById(id);
    }

    @Override
    public Integer updateOrderItem(OrderItem orderItem) {
        return orderItemMapper.updateOrderItem(orderItem);
    }

    @Override
    public Integer deleteOrderItemById(Long id) {
        return orderItemMapper.deleteOrderItemById(id);
    }

    @Override
    public List<OrderItem> selectOrderItemByOrderId(Long orderId) {
        return orderItemMapper.selectOrderItemByOrderId(orderId);
    }
}
