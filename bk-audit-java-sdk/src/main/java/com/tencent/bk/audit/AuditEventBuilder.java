package com.tencent.bk.audit;

import com.tencent.bk.audit.model.AuditEvent;

import java.util.List;

/**
 * 审计事件生成 Builder
 */
public interface AuditEventBuilder {
    List<AuditEvent> build();
}
