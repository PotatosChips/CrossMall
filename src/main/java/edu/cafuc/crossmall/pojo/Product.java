package edu.cafuc.crossmall.pojo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Product {
    private Long id;
    private String productName;
    private Long categoryId;
    private Long merchantId;
    private BigDecimal price;
    private Integer stock;
    private String image;
    private String description;
    private Integer status;
    private LocalDateTime create_time;
    private LocalDateTime update_time;
}
