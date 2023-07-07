package com.tencent.bk.audit.context;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class ActionAuditScopeImpl implements ActionAuditScope {

    private final ActionAuditContext beforeAttach;
    private final ActionAuditContext toAttach;
    private boolean closed;

    ActionAuditScopeImpl(ActionAuditContext beforeAttach, ActionAuditContext toAttach) {
        this.beforeAttach = beforeAttach;
        this.toAttach = toAttach;
    }

    @Override
    public void close() {
        try {
            if (!closed && ActionAuditContext.current() == toAttach) {
                closed = true;
                AuditContext.current().setCurrentActionAuditContext(beforeAttach);
            }
        } catch (Throwable e) {
            log.error("Close action audit scope caught exception", e);
        }
    }
}
