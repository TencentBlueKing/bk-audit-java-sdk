package com.tencent.bk.audit.config;

import com.tencent.bk.audit.*;
import com.tencent.bk.audit.exporter.EventExporter;
import com.tencent.bk.audit.exporter.LogFileExporter;
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
    private static final String EXPORTER_TYPE_LOG_FILE = "log_file";

    @Bean
    @ConditionalOnProperty(name = "audit.exporter.type", havingValue = EXPORTER_TYPE_LOG_FILE,
            matchIfMissing = true)
    LogFileExporter logFileEventExporter() {
        log.info("Init LogFileExporter");
        return new LogFileExporter();
    }

    @Bean
    Audit audit(EventExporter exporter) {
        log.info("Init Audit");
        return new Audit(exporter);
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
    public AuditAspect auditRecordAspect(Audit audit,
                                         AuditRequestProvider auditRequestProvider,
                                         AuditExceptionResolver auditExceptionResolver) {
        log.info("Init AuditAspect");
        return new AuditAspect(audit, auditRequestProvider, auditExceptionResolver);
    }

    @Bean
    public ActionAuditAspect actionAuditRecordAspect(Audit audit) {
        log.info("Init ActionAuditAspect");
        return new ActionAuditAspect(audit);
    }
}
