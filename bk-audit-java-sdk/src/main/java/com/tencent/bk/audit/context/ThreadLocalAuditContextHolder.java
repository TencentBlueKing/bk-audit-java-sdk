package com.tencent.bk.audit.context;

/**
 * 基于 ThreadLocal 实现的审计上下文托管
 */
public class ThreadLocalAuditContextHolder {

    private final ThreadLocal<AuditContext> AUDIT_CONTEXT_HOLDER = new ThreadLocal<>();

    /**
     * 设置为当前审计上下文
     *
     * @param auditContext 审计上下文
     */
    public void set(AuditContext auditContext) {
        AUDIT_CONTEXT_HOLDER.set(auditContext);
    }

    /**
     * 返回当前审计上下文
     *
     * @return 当前审计上下文
     */
    public AuditContext current() {
        return AUDIT_CONTEXT_HOLDER.get();
    }

    /**
     * 移除当前审计上下文
     */
    public void reset() {
        AUDIT_CONTEXT_HOLDER.remove();
    }
}
