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
    private Integer logisticsType;
    private Integer status;
    private String address;
    private String receiverName;
    private String receiverPhone;
    private LocalDateTime create_time;
    private LocalDateTime update_time;
}
