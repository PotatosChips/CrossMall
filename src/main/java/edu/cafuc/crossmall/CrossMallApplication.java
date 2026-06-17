package edu.cafuc.crossmall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@EnableRedisHttpSession
public class CrossMallApplication {
    public static void main(String[] args) {
        SpringApplication.run(CrossMallApplication.class, args);
    }
}