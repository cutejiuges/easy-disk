package com.cutejiuge.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 封装分页的响应结果
 *
 * @author cutejiuge
 * @since 2025/8/22 下午8:56
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageResult<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 3472446633861895596L;

    /**
     * 响应码
     */
    private Integer code;
    /**
     * 响应消息
     */
    private String message;
    /**
     * 分页数据
     */
    private PageData<T> data;
    /**
     * 时间戳
     */
    private Long timestamp;
    /**
     * traceId，链路追踪
     */
    private String traceId;

    /**
     * 无参构造
     */
    private PageResult() {
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 有参构造，携带响应码、响应消息、响应数据
     */
    private PageResult(Integer code, String message, PageData<T> data) {
        this();
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 成功响应
     */
    public static <T> PageResult<T> success(List<T> list, Pagination pagination) {
        PageData<T> pageData = new PageData<>();
        pageData.setList(list);
        pageData.setPagination(pagination);
        return new PageResult<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), pageData);
    }

    /**
     * 成功响应（自定义消息）
     */
    public static <T> PageResult<T> success(String message, List<T> list, Pagination pagination) {
        PageData<T> pageData = new PageData<>();
        pageData.setList(list);
        pageData.setPagination(pagination);
        return new PageResult<>(ResultCode.SUCCESS.getCode(), message, pageData);
    }

    /**
     * 失败响应
     */
    public static <T> PageResult<T> error(ResultCode resultCode) {
        return new PageResult<>(resultCode.getCode(), resultCode.getMessage(), null);
    }

    /**
     * 失败响应（自定义错误码和消息）
     */
    public static <T> PageResult<T> error(Integer code, String message) {
        return new PageResult<>(code, message, null);
    }

    /**
     * 设置链路追踪ID
     */
    public PageResult<T> traceId(String traceId) {
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
     * 分页数据内部类
     * @param <T>
     */
    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private static class PageData<T> implements Serializable {
        @Serial
        private static final long serialVersionUID = 1815534789593652367L;

        /**
         * 数据列表
         */
        private List<T> list;
        /**
         * 分页信息
         */
        private Pagination pagination;
    }
}
