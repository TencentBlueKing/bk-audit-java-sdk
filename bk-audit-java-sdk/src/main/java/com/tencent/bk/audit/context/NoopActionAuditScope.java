package com.tencent.bk.audit.context;

public class NoopActionAuditScope implements ActionAuditScope {
    @Override
    public void close() {
        // do nothing
    }
}
