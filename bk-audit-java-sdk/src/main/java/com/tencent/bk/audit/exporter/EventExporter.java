package com.tencent.bk.audit.exporter;

import com.tencent.bk.audit.model.AuditEvent;

import java.util.Collection;

/**
 * 审计事件输出
 */
public interface EventExporter {
    /**
     * 输出审计事件
     *
     * @param event 审计事件
     */
    void export(AuditEvent event);

    /**
     * 批量输出审计事件
     *
     * @param events 审计事件列表
     */
    void export(Collection<AuditEvent> events);
}
