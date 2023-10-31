package com.tencent.bk.audit.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class AuditMetrics {
    private final AtomicLong auditRequestTotal = new AtomicLong(0);
    private final AtomicLong auditExceptionRequestTotal = new AtomicLong(0);
    /**
     * 审计操作记录结果
     * key: audit request id ; value: 审计记录结果。成功：true，失败：false
     */
    private final Map<String, Boolean> auditRequestRecordResults = new ConcurrentHashMap<>();

    /**
     * 指标 - 触发审计操作的请求总数
     */
    private final String AUDIT_REQUEST_TOTAL = "audit_request_total";
    /**
     * 指标 - 审计操作记录失败总请求书
     */
    private final String AUDIT_EXCEPTION_REQUEST_TOTAL = "audit_exception_request_total";

    public AuditMetrics(MeterRegistry meterRegistry) {
        if (meterRegistry != null) {
            log.info("Init AuditMetrics with MeterRegistry");
            meterRegistry.gauge(AUDIT_REQUEST_TOTAL, Collections.emptyList(), this,
                    AuditMetrics::getAuditRequestTotal);
            meterRegistry.gauge(AUDIT_EXCEPTION_REQUEST_TOTAL, Collections.emptyList(), this,
                    AuditMetrics::getAuditExceptionRequestTotal);
        } else {
            log.info("Init AuditMetrics without MeterRegistry");
        }
    }

    public void recordAuditRequest(String requestId) {
        if (requestId == null) {
            return;
        }
        auditRequestRecordResults.put(requestId, true);
        this.auditRequestTotal.incrementAndGet();
    }

    public void recordAuditExceptionRequest(String requestId) {
        if (requestId == null) {
            return;
        }
        Boolean recordResult = auditRequestRecordResults.get(requestId);
        if (recordResult == null) {
            auditRequestRecordResults.put(requestId, recordResult);
            this.auditExceptionRequestTotal.incrementAndGet();
        }
    }

    public void clearAuditRequestTmpData(String requestId) {
        if (requestId == null) {
            return;
        }
        auditRequestRecordResults.remove(requestId);
    }

    public long getAuditRequestTotal() {
        return auditRequestTotal.get();
    }

    public long getAuditExceptionRequestTotal() {
        return auditExceptionRequestTotal.get();
    }
}
