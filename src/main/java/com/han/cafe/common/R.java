package com.han.cafe.common;

import lombok.Data;

@Data
public class R<T> {
    private Integer code; // 状态码
    private String message; // 提示信息
    private T data; // 数据
    private Long timestamp; // 时间戳

    private R() {
        this.timestamp = System.currentTimeMillis();
    }

    public static <T> R<T> ok() {
        return ok(null);
    }

    public static <T> R<T> ok(T data) {
        R<T> r = new R<>();
        r.setCode(200);
        r.setMessage("success");
        r.setData(data);
        return r;
    }

    public static <T> R<T> error() {
        return error(500, "服务器错误");
    }

    public static <T> R<T> error(String message) {
        return error(500, message);
    }

    public static <T> R<T> error(Integer code, String message) {
        R<T> r = new R<>();
        r.setCode(code);
        r.setMessage(message);
        return r;
    }

    public static <T> R<T> unauthorized(String message) {
        return error(401, message);
    }

    public static <T> R<T> forbidden(String message) {
        return error(403, message);
    }

    public static <T> R<T> badRequest(String message) {
        return error(400, message);
    }
} 