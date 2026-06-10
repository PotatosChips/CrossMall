package edu.cafuc.crossmall.pojo;

import lombok.Data;

import java.util.List;

@Data
public class Result {
    private Boolean success;
    private String massage;
    private Object data;      // 单条
    private List<?> list;     // 列表
    private Integer total;    // 分页总数

    public static Result ok() {
        Result r = new Result();
        r.setSuccess(true);
        return r;
    }

    public static Result okList(List<?> list, Integer total) {
        Result r = ok();
        r.setList(list);
        r.setTotal(total);
        return r;
    }

    public static Result okData(Object data) {
        Result r = ok();
        r.setData(data);
        return r;
    }

    public static Result fail(String massage) {
        Result r = new Result();
        r.setSuccess(false);
        r.setMassage(massage);
        return r;
    }
}