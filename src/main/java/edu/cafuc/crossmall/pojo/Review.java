package edu.cafuc.crossmall.pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Review {
    private Long id;
    private Long userId;
    private Long productId;
    private Long orderId;
    private Integer score;
    private String content;
    private LocalDateTime createTime;
}
