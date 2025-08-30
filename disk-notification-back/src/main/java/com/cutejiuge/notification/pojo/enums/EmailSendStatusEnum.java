package com.cutejiuge.notification.pojo.enums;

import lombok.Getter;

/**
 * 邮件发送状态枚举
 */
@Getter
public enum EmailSendStatusEnum {
    PENDING(0, "待发送"),
    SUCCESS(1, "发送成功"),
    FAILED(2, "发送失败");

    private final Integer code;
    private final String description;

    EmailSendStatusEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public static EmailSendStatusEnum fromCode(Integer code) {
        for (EmailSendStatusEnum status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return null;
    }
}
