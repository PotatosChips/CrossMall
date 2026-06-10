package edu.cafuc.crossmall.mapper;

import edu.cafuc.crossmall.pojo.Logistics;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LogisticsMapper {

    /** 根据订单 id 查询物流信息 */
    Logistics selectByOrderId(Long orderId);

    /** 插入物流信息 */
    Integer insertLogistics(Logistics logistics);

    /** 更新物流信息（补全公司、预计到达、状态等） */
    Integer updateLogistics(Logistics logistics);

    /** 换货补发：更新公司与运单号，重置为运输中 */
    Integer updateLogisticsReship(Logistics logistics);

    /** 查询当天最大运单号前缀，用于生成序号 */
    String selectMaxTrackingNoByPrefix(String prefix);
}
