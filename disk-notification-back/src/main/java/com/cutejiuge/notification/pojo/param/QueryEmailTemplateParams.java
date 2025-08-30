package com.cutejiuge.notification.pojo.param;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 查询邮件模板参数
 *
 * @author cutejiuge
 * @since 2025/8/26 上午9:24
 */
@Data
@Builder
@Accessors(chain = true)
public class QueryEmailTemplateParams {
    /**
     * 模板编码
     */
    private String templateCode;
}
