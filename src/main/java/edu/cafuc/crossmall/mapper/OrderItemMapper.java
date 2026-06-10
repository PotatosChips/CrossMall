package edu.cafuc.crossmall.mapper;

import edu.cafuc.crossmall.pojo.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface OrderItemMapper {

    /** 插入订单明细 */
    Integer insertOrderItem(@Param("orderId")Long orderId,
                            @Param("productId")Long productId,
                            @Param("productName")String productName,
                            @Param("price")BigDecimal price,
                            @Param("quantity")Integer quantity) ;

    /** 根据 id 删除订单所有明细 */
    Integer deleteOrderItemById(Long orderId);

    /** 查询某订单下的所有明细 卖家 */
    List<OrderItem> selectOrderItemForSeller(@Param("orderId")Long orderId,
                                       @Param("userId")Long userId);
    /** 查询某订单下的所有明细 买家*/
    List<OrderItem> selectOrderItemByOrderId(Long orderId);
}
