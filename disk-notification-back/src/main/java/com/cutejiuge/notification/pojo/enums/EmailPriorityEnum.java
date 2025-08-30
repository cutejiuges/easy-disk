package com.cutejiuge.notification.pojo.enums;

import lombok.Getter;

/**
 * 邮件优先级枚举
 *
 * @author cutejiuge
 * @since 2025/8/27 下午10:45
 */
@Getter
public enum EmailPriorityEnum {
    LOW(1, "低"),
    MEDIUM(2, "中"),
    HIGH(3, "高");

    private final Integer code;
    private final String description;

    EmailPriorityEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public static EmailPriorityEnum fromCode(Integer code) {
        for (EmailPriorityEnum priority : values()) {
            if (priority.code.equals(code)) {
                return priority;
            }
        }
        return null;
    }
}
