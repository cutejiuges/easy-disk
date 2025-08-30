package com.cutejiuge.notification.pojo.param;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 查询邮件记录参数
 *
 * @author cutejiuge
 * @since 2025/8/27 上午9:21
 */
@Data
@Builder
@Accessors(chain = true)
public class QueryEmailRecordParams {
    /**
     * 目标邮箱
     */
    private String toEmail;

    /**
     * 发送时间开始
     */
    private LocalDateTime startTime;

    /**
     * 发送结束时间
     */
    private LocalDateTime endTime;

    /**
     * 记录id
     */
    private Long id;

    /**
     * 发送状态
     */
    private Integer sendStatus;

    /**
     * 是否需要重试
     */
    private Boolean needRetry;

    /**
     * 最小创建时间
     */
    private LocalDateTime minCreatedAt;

    /**
     * 最大创建时间
     */
    private LocalDateTime maxCreatedAt;

    /**
     * 页数
     */
    private Integer page;

    /**
     * 每页大小
     */
    private Integer size;
}
