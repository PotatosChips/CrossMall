package edu.cafuc.crossmall.service;

import edu.cafuc.crossmall.pojo.OrderItem;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

public interface OrderItemService {

    /** 插入订单明细 */
    Integer insertOrderItem(Long orderId,Long productId,String productName, BigDecimal price,Integer quantity);

    /**  根据 id 删除订单明细 */
    Integer deleteOrderItemById(Long orderId);

    /** 查询某订单下的所有明细 卖家 */
    List<OrderItem> selectOrderItemForSeller(Long orderId,Long userId);

    /** T 查询某订单下的所有明细 */
    List<OrderItem> selectOrderItemByOrderId(Long orderId);
}
