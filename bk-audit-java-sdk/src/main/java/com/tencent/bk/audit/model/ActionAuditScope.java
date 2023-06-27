package com.tencent.bk.audit.model;

public interface ActionAuditScope extends AutoCloseable {
    @Override
    void close();
}
