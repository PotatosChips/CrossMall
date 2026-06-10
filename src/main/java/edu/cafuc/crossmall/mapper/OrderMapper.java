package edu.cafuc.crossmall.mapper;

import edu.cafuc.crossmall.pojo.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderMapper {

    /** 创建订单 */
    Integer insertOrder(Order order);

    /** 查询订单号 */
    String selectMaxOrderNoByPrefix(String prefix);

    /** 根据订单号查询订单（仅本人） */
    Order selectOrderByOrderNo(@Param("orderNo") String orderNo,
                               @Param("userId") Long userId);

    /** 更新订单（收货信息） */
    Integer updateReceiverInfo(@Param("orderNo") String orderNo,
                               @Param("userId") Long userId,
                               @Param("payType") Integer payType,
                               @Param("address") String address,
                               @Param("receiverName") String receiverName,
                               @Param("receiverPhone") String receiverPhone);

    /** 更新订单（支付状态） */
    Integer updateOrderStatus(@Param("orderNo") String orderNo,
                              @Param("userId") Long userId,
                              @Param("status") Integer status,
                              @Param("expectedStatus") Integer expectedStatus);

    /** 根据订单号删除待支付订单（仅本人） */
    Integer deleteOrderByOrderNo(@Param("orderNo") String orderNo,
                                 @Param("userId") Long userId);

    /** 查询某用户的订单列表 */
    List<Order> selectOrderByUserId(Long userId);

    /** 查询某  卖家商户  的  订单列表 */
    List<Order> selectOrderListForSeller(@Param("userId")Long userId);

    /** 卖家：订单包含该用户店铺商品时返回订单 */
    Order selectOrderForSeller(@Param("orderNo") String orderNo,
                               @Param("userId") Long sellerUserId);

    /** 卖家：按订单 id 校验店铺权限 */
    Order selectOrderForSellerByOrderId(@Param("orderId") Long orderId,
                                      @Param("userId") Long sellerUserId);
    /** 根据订单号查询（不校验买家，内部/卖家用） */
    Order selectOrderByOrderNoOnly(String orderNo);



    /** 按订单号更新状态（卖家发货等，不校验买家 userId） */
    Integer updateOrderStatusByOrderNo(@Param("orderNo") String orderNo,
                                       @Param("status") Integer status,
                                       @Param("expectedStatus") Integer expectedStatus);
}
