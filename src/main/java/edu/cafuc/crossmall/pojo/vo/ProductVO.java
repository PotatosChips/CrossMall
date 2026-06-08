package edu.cafuc.crossmall.pojo.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品展示用 VO，含关联的分类名、商家名、地区（列表页/详情页）
 */
@Data
public class ProductVO {
    private Long id;
    private String productName;
    private Long categoryId;
    private String categoryName;
    private Long merchantId;
    private String merchantName;
    private String region;
    private BigDecimal price;
    private Integer stock;
    private String image;
    private String description;
    private Integer status;
    private LocalDateTime create_time;
    private LocalDateTime update_time;
}
