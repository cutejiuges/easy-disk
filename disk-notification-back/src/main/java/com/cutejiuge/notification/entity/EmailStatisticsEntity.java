package com.cutejiuge.notification.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 邮件发送统计表
 * </p>
 *
 * @author cutejiuge
 * @since 2025-08-25 09:10:43
 */
@Getter
@Setter
@TableName("tb_email_statistics")
public class EmailStatisticsEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 统计日期
     */
    @TableField("stat_date")
    private LocalDate statDate;

    /**
     * 邮件类型：1-验证码，2-通知，3-营销
     */
    @TableField("email_type")
    private Integer emailType;

    /**
     * 总发送数
     */
    @TableField("total_count")
    private Integer totalCount;

    /**
     * 成功发送数
     */
    @TableField("success_count")
    private Integer successCount;

    /**
     * 失败发送数
     */
    @TableField("fail_count")
    private Integer failCount;

    /**
     * 成功率
     */
    @TableField("success_rate")
    private BigDecimal successRate;

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
