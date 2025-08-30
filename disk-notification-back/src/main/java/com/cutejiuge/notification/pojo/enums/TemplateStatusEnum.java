package com.cutejiuge.notification.pojo.enums;

import lombok.Getter;

/**
 * 模板状态枚举
 */
@Getter
public enum TemplateStatusEnum {
    DISABLED(0, "禁用"),
    ENABLED(1, "启用");

    private final Integer code;
    private final String description;

    TemplateStatusEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public static TemplateStatusEnum fromCode(Integer code) {
        for (TemplateStatusEnum status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return null;
    }
}
