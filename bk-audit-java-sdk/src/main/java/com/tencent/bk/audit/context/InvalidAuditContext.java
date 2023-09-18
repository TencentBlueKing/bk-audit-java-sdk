package com.tencent.bk.audit.context;

import com.tencent.bk.audit.constants.AccessTypeEnum;
import com.tencent.bk.audit.constants.UserIdentifyTypeEnum;
import com.tencent.bk.audit.model.AuditEvent;
import com.tencent.bk.audit.model.AuditHttpRequest;

import java.util.List;

/**
 * 非法的审计上下文（没有任何实际操作)
 */
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

    @Override
    public String getRequestId() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public UserIdentifyTypeEnum getUserIdentifyType() {
        return null;
    }

    @Override
    public String getUserIdentifyTenantId() {
        return null;
    }

    @Override
    public Long getStartTime() {
        return null;
    }

    @Override
    public Long getEndTime() {
        return null;
    }

    @Override
    public String getBkAppCode() {
        return null;
    }

    @Override
    public AccessTypeEnum getAccessType() {
        return null;
    }

    @Override
    public String getAccessSourceIp() {
        return null;
    }

    @Override
    public String getAccessUserAgent() {
        return null;
    }

    @Override
    public String getActionId() {
        return null;
    }

    @Override
    public List<String> getSubActionIds() {
        return null;
    }

    @Override
    public AuditHttpRequest getHttpRequest() {
        return null;
    }

    @Override
    public String getSystemId() {
        return null;
    }
}
