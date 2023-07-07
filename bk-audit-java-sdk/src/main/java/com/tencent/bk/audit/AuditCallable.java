package com.tencent.bk.audit;

@FunctionalInterface
public interface AuditCallable<V> {
    V call();
}
