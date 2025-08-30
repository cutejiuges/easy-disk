package com.cutejiuge.notification.pojo.enums;

import lombok.Getter;

/**
 * 模板类型枚举
 */
@Getter
public enum TemplateTypeEnum {
    VERIFICATION_CODE(1, "验证码"),
    NOTIFICATION(2, "通知"),
    MARKETING(3, "营销");

    private final Integer code;
    private final String description;

    TemplateTypeEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public static TemplateTypeEnum fromCode(Integer code) {
        for (TemplateTypeEnum type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }
}
