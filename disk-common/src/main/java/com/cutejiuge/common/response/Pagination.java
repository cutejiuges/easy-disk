package com.cutejiuge.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 包装分页信息
 *
 * @author cutejiuge
 * @since 2025/8/22 下午8:45
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Pagination implements Serializable {
    @Serial
    private static final long serialVersionUID = 3985845013317735594L;

    /**
     * 当前页码
     */
    private Integer page;
    /**
     * 每页数据量
     */
    private Integer size;
    /**
     * 数据总数
     */
    private Long total;
    /**
     * 分页总数
     */
    private Integer pages;
    /**
     * 有前一页
     */
    private Boolean hasPrevious;
    /**
     * 有后一页
     */
    private Boolean hasNext;

    /**
     * 无参构造
     */
    public Pagination() {}

    /**
     * 有参构造，传入页数、大小、记录数
     */
    public Pagination(Integer page, Integer size, Long total) {
        this.page = page;
        this.size = size;
        this.total = total;
        this.pages = (int) Math.ceil((double) total / size);
        this.hasPrevious = page > 1;
        this.hasNext = this.pages > page;
    }

    /**
     * 静态构造，创建分页信息
     */
    public static Pagination of(Integer page, Integer size, Long total) {
        return new Pagination(page, size, total);
    }

    /**
     * 获取偏移量
     */
    public Long getOffset() {
        return (long) (page - 1) * size;
    }
}
