package edu.cafuc.crossmall.service;

import edu.cafuc.crossmall.pojo.Order;

import java.util.List;

public interface OrderService {

    /** TODO: 创建订单 */
    Integer insertOrder(Order order);

    /** TODO: 根据 id 查询订单 */
    Order selectOrderById(Long id);

    /** TODO: 更新订单（如改状态、支付方式等） */
    Integer updateOrder(Order order);

    /** TODO: 根据 id 删除订单 */
    Integer deleteOrderById(Long id);

    /** TODO: 查询某用户的订单列表 */
    List<Order> selectOrderByUserId(Long userId);
}
