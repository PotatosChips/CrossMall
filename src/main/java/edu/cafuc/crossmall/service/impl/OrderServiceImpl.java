package edu.cafuc.crossmall.service.impl;

import edu.cafuc.crossmall.mapper.OrderMapper;
import edu.cafuc.crossmall.pojo.Order;
import edu.cafuc.crossmall.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Override
    public Integer insertOrder(Order order) {
        return orderMapper.insertOrder(order);
    }

    @Override
    public Order selectOrderById(Long id) {
        return orderMapper.selectOrderById(id);
    }

    @Override
    public Integer updateOrder(Order order) {
        return orderMapper.updateOrder(order);
    }

    @Override
    public Integer deleteOrderById(Long id) {
        return orderMapper.deleteOrderById(id);
    }

    @Override
    public List<Order> selectOrderByUserId(Long userId) {
        return orderMapper.selectOrderByUserId(userId);
    }
}
