package com.cutejiuge.notification.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 消息队列任务表
 * </p>
 *
 * @author cutejiuge
 * @since 2025-08-25 09:10:43
 */
@Getter
@Setter
@TableName("tb_message_task")
public class MessageTaskEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 任务ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 任务类型：1-邮件发送，2-短信发送，3-推送通知
     */
    @TableField("task_type")
    private Integer taskType;

    /**
     * 任务数据(JSON格式)
     */
    @TableField("task_data")
    private String taskData;

    /**
     * 任务状态：0-待处理，1-处理中，2-成功，3-失败
     */
    @TableField("task_status")
    private Integer taskStatus;

    /**
     * 重试次数
     */
    @TableField("retry_count")
    private Integer retryCount;

    /**
     * 最大重试次数
     */
    @TableField("max_retry_count")
    private Integer maxRetryCount;

    /**
     * 下次重试时间
     */
    @TableField("next_retry_time")
    private LocalDateTime nextRetryTime;

    /**
     * 错误信息
     */
    @TableField("error_message")
    private String errorMessage;

    /**
     * 执行时间
     */
    @TableField("execute_time")
    private LocalDateTime executeTime;

    /**
     * 完成时间
     */
    @TableField("complete_time")
    private LocalDateTime completeTime;

    /**
     * 创建时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 删除时间
     */
    @TableField("deleted_at")
    @TableLogic(value = "null", delval = "now()")
    private LocalDateTime deletedAt;
}
