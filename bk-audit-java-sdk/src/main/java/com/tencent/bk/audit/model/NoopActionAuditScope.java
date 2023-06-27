package com.tencent.bk.audit.model;

public class NoopActionAuditScope implements ActionAuditScope {
    @Override
    public void close() {
        // do nothing
    }
}
