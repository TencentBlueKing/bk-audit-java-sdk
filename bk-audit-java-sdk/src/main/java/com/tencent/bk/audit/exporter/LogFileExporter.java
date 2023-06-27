package com.tencent.bk.audit.exporter;

import com.tencent.bk.audit.constants.Constants;
import com.tencent.bk.audit.model.AuditEvent;
import com.tencent.bk.audit.utils.EventIdGenerator;
import com.tencent.bk.audit.utils.json.JsonUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * 审计事件 - 审计日志文件方式输出
 */
public class LogFileExporter implements EventExporter {
    private final Logger LOGGER;

    public LogFileExporter() {
        this.LOGGER = LoggerFactory.getLogger(Constants.AUDIT_LOGGER_NAME);
    }

    @Override
    public void export(AuditEvent event) {
        if (StringUtils.isBlank(event.getId())) {
            event.setId(EventIdGenerator.generateId());
        }
        LOGGER.info(JsonUtils.toJson(event));
    }

    @Override
    public void export(Collection<AuditEvent> events) {
        if (CollectionUtils.isEmpty(events)) {
            return;
        }
        events.forEach(event -> LOGGER.info(JsonUtils.toJson(event)));
    }
}
