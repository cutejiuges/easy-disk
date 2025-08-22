package com.cutejiuge.common.exception;

import com.cutejiuge.common.response.ResultCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 定义常见业务异常
 *
 * @author cutejiuge
 * @since 2025/8/21 下午11:54
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BusinessException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = 6578908724396656621L;

    /**
     * 错误码
     */
    private Integer code;

    /**
     * 错误信息
     */
    private String message;

    /**
     * 构造方法, 无参
     */
    public BusinessException() {
        super();
    }

    /**
     * 构造方法，传入自定义消息
     * @param message 自定义消息
     */
    public BusinessException(String message) {
        super(message);
        this.code = ResultCode.INTERNAL_SERVER_ERROR.getCode();
        this.message = message;
    }

    /**
     * 构造方法，传入错误码和消息
     * @param code 错误码
     * @param message 自定义消息
     */
    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    /**
     * 构造方法，传入异常枚举
     * @param resultCode 异常枚举
     */
    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
    }

    /**
     * 构造方法，传入异常枚举和自定义消息
     * @param resultCode 异常枚举
     * @param message 自定义消息
     */
    public BusinessException(ResultCode resultCode, String message) {
        super(message);
        this.code = resultCode.getCode();
        this.message = message;
    }

    /**
     * 构造方法，传入自定义消息和异常原因
     * @param message 自定义消息
     * @param cause 异常原因
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.code = ResultCode.INTERNAL_SERVER_ERROR.getCode();
        this.message = message;
    }

    /**
     * 构造方法，传入错误码、自定义消息、异常原因
     * @param code 错误码
     * @param message 自定义消息
     * @param cause 异常原因
     */
    public BusinessException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }

    /**
     * 构造方法，传入异常枚举和异常原因
     * @param resultCode 异常枚举
     * @param cause 异常原因
     */
    public BusinessException(ResultCode resultCode, Throwable cause) {
        super(resultCode.getMessage(), cause);
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
    }

    /**
     * 静态工厂方法 - 创建业务异常
     */
    public static BusinessException of(String message) {
        return new BusinessException(message);
    }

    /**
     * 静态工厂方法 - 创建业务异常
     */
    public static BusinessException of(Integer code, String message) {
        return new BusinessException(code, message);
    }

    /**
     * 静态工厂方法 - 创建业务异常
     */
    public static BusinessException of(ResultCode resultCode) {
        return new BusinessException(resultCode);
    }

    /**
     * 静态工厂方法 - 创建业务异常
     */
    public static BusinessException of(ResultCode resultCode, String message) {
        return new BusinessException(resultCode, message);
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
