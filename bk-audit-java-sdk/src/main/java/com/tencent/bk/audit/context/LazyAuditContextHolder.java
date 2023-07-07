package com.tencent.bk.audit.context;

/**
 * AuditContext 托管 - 懒加载方式
 */
public class LazyAuditContextHolder {
    private static final ThreadLocalAuditContextHolder storage;

    public static ThreadLocalAuditContextHolder get() {
        return storage;
    }

    static {
        storage = new ThreadLocalAuditContextHolder();
    }
}
