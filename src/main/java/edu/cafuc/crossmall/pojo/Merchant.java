package edu.cafuc.crossmall.pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Merchant {
    private Long id;
    private String merchantName;
    private String region;
    private String description;
    private Long user_id;
    private LocalDateTime create_time;
    private LocalDateTime update_time;
}
