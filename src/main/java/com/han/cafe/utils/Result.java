package com.han.cafe.utils;

import lombok.Data;

/**
 * 统一返回结果类
 * @param <T> 数据类型
 */
@Data
public class Result<T> {
    
    /**
     * 状态码
     */
    private Integer code;
    
    /**
     * 消息
     */
    private String message;
    
    /**
     * 数据
     */
    private T data;
    
    /**
     * 时间戳
     */
    private Long timestamp;
    
    /**
     * 私有构造方法
     */
    private Result() {
        this.timestamp = System.currentTimeMillis();
    }
    
    /**
     * 带数据的成功结果
     * @param data 数据
     * @param <T> 数据类型
     * @return 成功结果
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("操作成功");
        result.setData(data);
        return result;
    }
    
    /**
     * 不带数据的成功结果
     * @return 成功结果
     */
    public static <T> Result<T> success() {
        return success(null);
    }
    
    /**
     * 带消息的成功结果
     * @param message 消息
     * @return 成功结果
     */
    public static <T> Result<T> successMsg(String message) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage(message);
        return result;
    }
    
    /**
     * 带数据和消息的成功结果
     * @param data 数据
     * @param message 消息
     * @param <T> 数据类型
     * @return 成功结果
     */
    public static <T> Result<T> success(T data, String message) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage(message);
        result.setData(data);
        return result;
    }
    
    /**
     * 带消息的失败结果
     * @param message 消息
     * @return 失败结果
     */
    public static <T> Result<T> fail(String message) {
        Result<T> result = new Result<>();
        result.setCode(500);
        result.setMessage(message);
        return result;
    }
    
    /**
     * 带状态码和消息的失败结果
     * @param code 状态码
     * @param message 消息
     * @return 失败结果
     */
    public static <T> Result<T> fail(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
    
    /**
     * 默认失败结果
     * @return 失败结果
     */
    public static <T> Result<T> fail() {
        return fail(500, "服务器错误");
    }
    
    /**
     * 未授权结果
     * @param message 消息
     * @return 未授权结果
     */
    public static <T> Result<T> unauthorized(String message) {
        return fail(401, message);
    }
    
    /**
     * 禁止访问结果
     * @param message 消息
     * @return 禁止访问结果
     */
    public static <T> Result<T> forbidden(String message) {
        return fail(403, message);
    }
    
    /**
     * 请求错误结果
     * @param message 消息
     * @return 请求错误结果
     */
    public static <T> Result<T> badRequest(String message) {
        return fail(400, message);
    }
    
    /**
     * 资源不存在结果
     * @param message 消息
     * @return 资源不存在结果
     */
    public static <T> Result<T> notFound(String message) {
        return fail(404, message);
    }
} 