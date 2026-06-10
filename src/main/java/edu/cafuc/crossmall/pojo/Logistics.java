package edu.cafuc.crossmall.pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Logistics {
    private Long id;
    private Long orderId;
    private String company;
    private String trackingNo;
    /** 0待发货 1运输中 2已签收 */
    private Integer status;
    private LocalDateTime estimatedArrival;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
