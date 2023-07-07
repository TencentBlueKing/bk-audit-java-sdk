package com.tencent.bk.audit.exporter;

import com.tencent.bk.audit.constants.Constants;
import com.tencent.bk.audit.model.AuditEvent;
import com.tencent.bk.audit.utils.EventIdGenerator;
import com.tencent.bk.audit.utils.json.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.NOPLogger;

import java.util.Collection;

/**
 * 审计事件 - 审计日志文件方式输出
 */
@Slf4j
public class LogFileEventExporter implements EventExporter {
    private final Logger LOGGER;

    public LogFileEventExporter() {
        this.LOGGER = LoggerFactory.getLogger(Constants.AUDIT_LOGGER_NAME);
        if (this.LOGGER == null || this.LOGGER instanceof NOPLogger) {
            log.error("Invalid LogFileExporter");
        }
    }

    @Override
    public void export(AuditEvent event) {
        if (StringUtils.isBlank(event.getId())) {
            event.setId(EventIdGenerator.generateId());
        }
        try {
            LOGGER.info(JsonUtils.toJson(event));
        } catch (Throwable throwable) {
            log.error("Export audit event exception", throwable);
        }
    }

    @Override
    public void export(Collection<AuditEvent> events) {
        if (CollectionUtils.isEmpty(events)) {
            return;
        }
        events.forEach(this::export);
    }
}
