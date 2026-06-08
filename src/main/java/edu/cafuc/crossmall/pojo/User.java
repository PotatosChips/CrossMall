package edu.cafuc.crossmall.pojo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String nickname;
    private String phone;
    private Integer role;
    private Integer status;
    private LocalDateTime create_time;
    private LocalDateTime update_time;
}
//        id          BIGINT PRIMARY KEY AUTO_INCREMENT,
//        username    VARCHAR(50)  NOT NULL UNIQUE COMMENT '用户名',
//        password    VARCHAR(100) NOT NULL COMMENT '密码（加密后）',
//        nickname    VARCHAR(50)  COMMENT '昵称',
//        phone       VARCHAR(20)  COMMENT '手机号',
//        role        TINYINT DEFAULT 0 COMMENT '0买家 1卖家 2管理员',
//        status      TINYINT DEFAULT 1 COMMENT '0禁用 1正常',
//        create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
//        update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP