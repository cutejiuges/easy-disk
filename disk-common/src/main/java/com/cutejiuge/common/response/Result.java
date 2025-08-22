package com.cutejiuge.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 包装统一返回结构
 *
 * @author cutejiuge
 * @since 2025/8/22 上午12:34
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 4405717622670639377L;

    /** 响应码 */
    private Integer code;
    /** 响应消息 */
    private String message;
    /** 响应数据 */
    private T data;
    /** 时间戳 */
    private Long timestamp;
    /** traceId链路追踪 */
    private String traceId;
    /** 错误实体，有问题的时候才会带上 */
    private Object error;

    /**
     * 私有构造方法，仅赋值时间
     */
    private Result() {
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 私有构造方法，赋值响应码、响应消息和响应数据
     */
    private Result(int code, String message, T data) {
        this();
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 成功响应，不携带数据
     */
    public static <T> Result<T> success() {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), null);
    }

    /**
     * 成功响应，携带数据
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }

    /**
     * 成功响应，携带自定义消息和数据
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), message, data);
    }

    /**
     * 失败响应，使用错误码枚举
     */
    public static <T>  Result<T> error(ResultCode resultCode) {
        return new Result<>(resultCode.getCode(), resultCode.getMessage(), null);
    }

    /**
     * 失败响应，自定义错误码和消息
     */
    public static <T>  Result<T> error(int code, String message) {
        return new Result<>(code, message, null);
    }

    /**
     * 失败响应，自定义错误消息
     */
    public static <T> Result<T> error(String message) {
        return new Result<>(ResultCode.INTERNAL_SERVER_ERROR.getCode(), message, null);
    }

    /**
     * 参数验证失败响应
     */
    public static <T> Result<T> validationError(String message, Object errors) {
        Result<T> result = new Result<>(ResultCode.PARAM_VALIDATION_ERROR.getCode(), message, null);
        result.setError(errors);
        return result;
    }

    /**
     * 设置链路追踪ID
     */
    public Result<T> traceId(String traceId) {
        this.traceId = traceId;
        return this;
    }

    /**
     * 判断是否成功
     */
    public boolean isSuccess() {
        return ResultCode.SUCCESS.getCode().equals(this.code);
    }

    /**
     * 判断是否失败
     */
    public boolean isError() {
        return !isSuccess();
    }
}
