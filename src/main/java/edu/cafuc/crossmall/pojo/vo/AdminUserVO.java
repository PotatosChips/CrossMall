package edu.cafuc.crossmall.pojo.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminUserVO {
    private Long id;
    private String username;
    private String nickname;
    private Integer role;
    private Integer status;
    private LocalDateTime createTime;
}
