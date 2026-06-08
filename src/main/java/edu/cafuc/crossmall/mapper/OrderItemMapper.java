package edu.cafuc.crossmall.mapper;

import edu.cafuc.crossmall.pojo.OrderItem;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderItemMapper {

    /** TODO: 插入订单明细 */
    Integer insertOrderItem(OrderItem orderItem);

    /** TODO: 根据 id 查询订单明细 */
    OrderItem selectOrderItemById(Long id);

    /** TODO: 更新订单明细 */
    Integer updateOrderItem(OrderItem orderItem);

    /** TODO: 根据 id 删除订单明细 */
    Integer deleteOrderItemById(Long id);

    /** TODO: 查询某订单下的所有明细 */
    List<OrderItem> selectOrderItemByOrderId(Long orderId);
}
