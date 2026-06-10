package edu.cafuc.crossmall.pojo.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AfterSaleVO {
    private Long id;
    private Long orderId;
    private Long userId;
    private Integer type;
    private String reason;
    private Integer status;
    private String reply;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String orderNo;
    private String buyerNickname;
    private Integer orderStatus;
}
