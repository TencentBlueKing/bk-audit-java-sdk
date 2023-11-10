package com.tencent.bk.audit.filter;

import com.tencent.bk.audit.model.AuditEvent;

/**
 * 审计结束之前触发的 Filter，允许修改最终的审计事件。
 */
public interface AuditPostFilter {
    /**
     * 修改审计事件
     *
     * @param auditEvent 审计事件
     * @return 修改之后的审计事件
     */
    default AuditEvent map(AuditEvent auditEvent) {
        return auditEvent;
    }
}
