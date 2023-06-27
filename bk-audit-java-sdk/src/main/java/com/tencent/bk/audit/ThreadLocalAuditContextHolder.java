package com.tencent.bk.audit;

import com.tencent.bk.audit.model.AuditContext;

/**
 * 基于 ThreadLocal 实现的当前审计上下文托管
 */
public class ThreadLocalAuditContextHolder {

    private final ThreadLocal<AuditContext> AUDIT_CONTEXT_HOLDER = new ThreadLocal<>();

    public void set(AuditContext auditContext) {
        AUDIT_CONTEXT_HOLDER.set(auditContext);
    }

    public AuditContext current() {
        return AUDIT_CONTEXT_HOLDER.get();
    }

    public void reset() {
        AUDIT_CONTEXT_HOLDER.remove();
    }
}
