package com.tencent.bk.audit.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class AuditMetrics {

    /**
     * 指标 - 触发审计操作的请求总数
     */
    private final String AUDIT_REQUEST_TOTAL = "audit_request_total";
    /**
     * 指标 - 审计操作记录失败总数
     */
    private final String AUDIT_EXCEPTION_TOTAL = "audit_exception_total";

    private final MeterRegistry meterRegistry;

    public AuditMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    /**
     * 记录审计操作请求数量
     *
     * @param actionId 操作 ID
     */
    public void recordAuditRequest(String actionId) {
        if (meterRegistry != null) {
            meterRegistry.counter(AUDIT_REQUEST_TOTAL, buildTags(actionId)).increment();
        } else {
            if (log.isDebugEnabled()) {
                log.debug("MeterRegistry is not available, skip record audit metrics");
            }
        }
    }

    private Iterable<Tag> buildTags(String actionId) {
        return Tags.of("action", StringUtils.isEmpty(actionId) ? "Unknown" : actionId);
    }

    /**
     * 记录审计日志记录异常数量
     *
     * @param actionId 操作 ID
     */
    public void recordAuditException(String actionId) {
        if (meterRegistry != null) {
            meterRegistry.counter(AUDIT_EXCEPTION_TOTAL, buildTags(actionId)).increment();
        } else {
            if (log.isDebugEnabled()) {
                log.debug("MeterRegistry is not available, skip record audit metrics");
            }
        }
    }
}
