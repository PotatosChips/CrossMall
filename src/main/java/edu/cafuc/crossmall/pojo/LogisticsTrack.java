package edu.cafuc.crossmall.pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LogisticsTrack {
    private Long id;
    private Long logisticsId;
    private String content;
    private LocalDateTime trackTime;
    private LocalDateTime createTime;
}
