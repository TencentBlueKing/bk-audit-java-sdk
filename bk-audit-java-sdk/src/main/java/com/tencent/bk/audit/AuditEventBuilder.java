package com.tencent.bk.audit;

import com.tencent.bk.audit.model.AuditEvent;

import java.util.List;

public interface AuditEventBuilder {
    List<AuditEvent> build();
}
