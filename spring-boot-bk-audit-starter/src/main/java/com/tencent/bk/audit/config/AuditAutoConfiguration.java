package com.tencent.bk.audit.config;

import com.tencent.bk.audit.*;
import com.tencent.bk.audit.constants.ExporterTypeEnum;
import com.tencent.bk.audit.exporter.EventExporter;
import com.tencent.bk.audit.exporter.LogFileEventExporter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(AuditProperties.class)
@ConditionalOnProperty(name = "audit.enabled", havingValue = "true", matchIfMissing = true)
@Slf4j
public class AuditAutoConfiguration {

    /**
     * 审计事件默认 Exporter : 文件日志
     */
    @Bean
    @ConditionalOnProperty(name = "audit.exporter.type", havingValue = ExporterTypeEnum.Constants.LOG_FILE,
            matchIfMissing = true)
    @ConditionalOnMissingBean(value = EventExporter.class)
    LogFileEventExporter logFileEventExporter() {
        log.info("Init LogFileExporter");
        return new LogFileEventExporter();
    }

    @Bean
    AuditClient audit(EventExporter exporter, AuditExceptionResolver auditExceptionResolver) {
        log.info("Init Audit");
        return new AuditClient(exporter, auditExceptionResolver);
    }

    @Bean
    @ConditionalOnMissingBean(AuditRequestProvider.class)
    public AuditRequestProvider auditRequestProvider() {
        log.info("Init DefaultAuditRequestProvider");
        return new DefaultAuditRequestProvider();
    }

    @Bean
    @ConditionalOnMissingBean(AuditExceptionResolver.class)
    public DefaultAuditExceptionResolver auditExceptionResolver() {
        log.info("Init DefaultAuditExceptionResolver");
        return new DefaultAuditExceptionResolver();
    }

    @Bean
    public AuditAspect auditRecordAspect(AuditClient auditClient,
                                         AuditRequestProvider auditRequestProvider,
                                         AuditExceptionResolver auditExceptionResolver,
                                         AuditProperties auditProperties) {
        log.info("Init AuditAspect");
        return new AuditAspect(auditClient, auditRequestProvider, auditExceptionResolver, auditProperties);
    }

    @Bean
    public ActionAuditAspect actionAuditRecordAspect(AuditClient auditClient) {
        log.info("Init ActionAuditAspect");
        return new ActionAuditAspect(auditClient);
    }
}
