package edu.cafuc.crossmall.mapper;

import edu.cafuc.crossmall.pojo.Payment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PaymentMapper {

    /** 插入支付记录 */
    Integer insertPayment(Payment payment);

    /** 根据订单 id 查询支付记录 */
    Payment selectByOrderId(Long orderId);

    /** 待支付记录完成支付（status 0→1） */
    Integer updatePaymentSuccess(Payment payment);

    /** 查询当天最大流水号前缀，用于生成 payNo */
    String selectMaxPayNoByPrefix(String prefix);

    /** 模拟退款（status 1→2） */
    Integer updatePaymentRefund(Long orderId);
}
