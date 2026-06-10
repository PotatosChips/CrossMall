package edu.cafuc.crossmall.pojo.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 店铺展示用 VO（不含 user_id）
 */
@Data
public class MerchantVO {
    private Long id;
    private String merchantName;
    private String region;
    private String description;
    private Integer productCount;
    private LocalDateTime createTime;
}
