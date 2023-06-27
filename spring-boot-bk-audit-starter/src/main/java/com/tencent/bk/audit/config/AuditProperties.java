package com.tencent.bk.audit.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 审计中心配置
 */
@ConfigurationProperties(prefix = "audit")
@Getter
@Setter
@ToString
public class AuditProperties {
    /**
     * 是否启用操作审计。默认启用
     */
    private boolean enabled = true;

    /**
     * 审计事件 Exporter 配置
     */
    private Exporter exporter;

    @Getter
    @Setter
    @ToString
    private static class Exporter {
        private String type;
    }
}
