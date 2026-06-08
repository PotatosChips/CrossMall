package edu.cafuc.crossmall.pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Category {
    private Long id;
    private String categoryName;
    private Integer sort;
    private LocalDateTime create_time;
}
