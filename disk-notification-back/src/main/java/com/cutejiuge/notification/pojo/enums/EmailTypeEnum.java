package com.cutejiuge.notification.pojo.enums;

import lombok.Getter;

/**
 * 邮件类型枚举
 *
 * @author cutejiuge
 * @since 2025/8/27 下午10:43
 */
@Getter
public enum EmailTypeEnum {
    VERIFICATION_CODE(1, "验证码"),
    NOTIFICATION(2, "通知"),
    MARKETING(3, "营销");

    private final Integer code;
    private final String description;

    EmailTypeEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public static EmailTypeEnum fromCode(Integer code) {
        for (EmailTypeEnum type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }
}
