package edu.cafuc.crossmall.pojo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Payment {
    private Long id;
    private Long orderId;
    /** 第三方支付流水号（模拟） */
    private String payNo;
    /** 1支付宝 2微信 3信用卡 */
    private Integer payType;
    private BigDecimal amount;
    /** 0待支付 1支付成功 2已退款 */
    private Integer status;
    private LocalDateTime payTime;
    private LocalDateTime createTime;
}
