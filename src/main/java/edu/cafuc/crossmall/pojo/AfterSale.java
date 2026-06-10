package edu.cafuc.crossmall.pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AfterSale {
    private Long id;
    private Long orderId;
    private Long userId;
    /** 1退货退款 2换货 3投诉 4仅退款 */
    private Integer type;
    private String reason;
    /** 0待处理 1处理中 2已完成 3已拒绝 */
    private Integer status;
    private String reply;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
