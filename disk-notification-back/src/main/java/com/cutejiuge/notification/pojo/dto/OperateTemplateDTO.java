package com.cutejiuge.notification.pojo.dto;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

/**
 * 渲染模板参数
 *
 * @author cutejiuge
 * @since 2025/8/26 上午8:46
 */
@Data
@Builder
@Accessors(chain = true)
public class OperateTemplateDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 4567728868795568847L;

    /**
     * 模板编码
     */
    private String templateCode;

    /**
     * 模板变量
     */
    private Map<String, Object> variables;
}
