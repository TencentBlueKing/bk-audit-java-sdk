package com.tencent.bk.audit.context;

public interface ActionAuditScope extends AutoCloseable {
    @Override
    void close();
}
