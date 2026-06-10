package edu.cafuc.crossmall.pojo.vo;

import edu.cafuc.crossmall.pojo.Logistics;
import edu.cafuc.crossmall.pojo.LogisticsTrack;
import edu.cafuc.crossmall.pojo.OrderItem;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单详情 VO，含订单头 + 订单明细 + 物流信息
 */
@Data
public class OrderVO {
    private Long id;
    private String orderNo;
    private Long userId;
    private BigDecimal totalAmount;
    private Integer payType;
    private Integer status;
    private String address;
    private String receiverName;
    private String receiverPhone;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<OrderItem> items;
    /** 物流信息，未发货时为 null */
    private Logistics logistics;
    /** 物流轨迹，无物流时为空列表 */
    private List<LogisticsTrack> tracks;
}
