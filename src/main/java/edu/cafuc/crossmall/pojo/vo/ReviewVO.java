package edu.cafuc.crossmall.pojo.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReviewVO {
    private Long id;
    private Long userId;
    private Long productId;
    private Long orderId;
    private Integer score;
    private String content;
    private LocalDateTime createTime;
    private String nickname;
}
