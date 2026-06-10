package edu.cafuc.crossmall.pojo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Order {
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
    /** 列表联查 logistics.status（买家/卖家列表均可能填充） */
    private Integer logisticsStatus;
}
