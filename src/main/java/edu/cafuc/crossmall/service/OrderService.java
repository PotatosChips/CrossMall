package edu.cafuc.crossmall.service;

import edu.cafuc.crossmall.pojo.Logistics;
import edu.cafuc.crossmall.pojo.Order;
import edu.cafuc.crossmall.pojo.vo.OrderVO;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderService {

    /**  创建订单 */
    Order addOrder(Long userId,Integer payType,Integer status,String address,String receiverName,String receiverPhone );

    /** 更新订单（收货信息） */
    Integer updateReceiverInfo(String orderNo,Long userId,Integer payType,String address,String receiverName,String receiverPhone);

    /** 更新订单确认 */
    Integer confirmReceipt(String orderNo, Long userId);

    /** 根据订单号  删除  待支付订单（仅本人） */
    Integer deleteOrderByOrderNo(String orderNo,Long userId);

    /** 查询某  用户  的  订单列表 */
    List<Order> selectOrderByUserId(Long userId);

    /** 查询某  卖家商户  的  订单列表 */
    List<Order> selectOrderListForSeller(Long userId);

    /** 根据订单号查询  订单详情  （含明细，仅本人） */
    OrderVO selectOrderByOrderNo(String orderNo, Long userId);

    /** 卖家：订单包含该用户店铺商品时返回订单 */
    OrderVO selectOrderForSeller(String orderNo, Long sellerUserId);

    /** 买家模拟支付（0→1）：写 payment 表 + 更新订单状态，见 OrderServiceImpl.payOrder */
    Integer payOrder(String orderNo, Long userId);

    /** 卖家发货：创建物流、自动生成运单号、订单 1→2 */
    Logistics shipOrder(String orderNo, Long sellerUserId, String company, LocalDateTime estimatedArrival, String content);

    /** 卖家补全/更新物流信息 */
    Integer updateLogistics(String orderNo, Long sellerUserId, String company, LocalDateTime estimatedArrival, Integer status);

    /** 卖家追加物流轨迹 */
    Integer addLogisticsTrack(String orderNo, Long sellerUserId, String content, LocalDateTime trackTime);

    /** 换货补发：重置同一条 logistics 并追加轨迹，order.status 不变 */
    Logistics reshipForExchange(String orderNo, Long sellerUserId, String company);
}
