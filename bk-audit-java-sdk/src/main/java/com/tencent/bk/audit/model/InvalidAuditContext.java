package com.tencent.bk.audit.model;

import java.util.List;

public class InvalidAuditContext implements AuditContext {
    @Override
    public ActionAuditContext currentActionAuditContext() {
        return ActionAuditContext.INVALID;
    }

    @Override
    public void addActionAuditContext(ActionAuditContext actionAuditContext) {

    }

    @Override
    public void end() {

    }

    @Override
    public void setCurrentActionAuditContext(ActionAuditContext actionAuditContext) {

    }

    @Override
    public void error(int resultCode, String resultDesc) {

    }

    @Override
    public List<AuditEvent> getEvents() {
        return null;
    }

    @Override
    public void updateActionId(String actionId) {

    }
}
