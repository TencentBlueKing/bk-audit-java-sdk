package com.tencent.bk.audit.context;

import com.tencent.bk.audit.constants.AccessTypeEnum;
import com.tencent.bk.audit.constants.UserIdentifyTypeEnum;
import com.tencent.bk.audit.model.AuditHttpRequest;

import java.util.List;

public class AuditContextBuilder {

    private String requestId;

    private String username;

    private UserIdentifyTypeEnum userIdentifyType;

    private String userIdentifyTenantId;

    private String bkAppCode;

    private String systemId;

    private AccessTypeEnum accessType;

    private String accessSourceIp;

    private String accessUserAgent;

    /**
     * 操作ID
     */
    private final String actionId;

    /**
     * 审计操作 Http 请求
     */
    private AuditHttpRequest httpRequest;

    /**
     * 子操作
     */
    private List<String> subActionIds;


    public static AuditContextBuilder builder(String actionId) {
        return new AuditContextBuilder(actionId);
    }

    private AuditContextBuilder(String actionId) {
        this.actionId = actionId;
    }

    public AuditContextBuilder setRequestId(String requestId) {
        this.requestId = requestId;
        return this;
    }

    public AuditContextBuilder setUsername(String username) {
        this.username = username;
        return this;
    }

    public AuditContextBuilder setUserIdentifyType(UserIdentifyTypeEnum userIdentifyType) {
        this.userIdentifyType = userIdentifyType;
        return this;
    }

    public AuditContextBuilder setUserIdentifyTenantId(String userIdentifyTenantId) {
        this.userIdentifyTenantId = userIdentifyTenantId;
        return this;
    }

    public AuditContextBuilder setBkAppCode(String bkAppCode) {
        this.bkAppCode = bkAppCode;
        return this;
    }

    public AuditContextBuilder setAccessType(AccessTypeEnum accessType) {
        this.accessType = accessType;
        return this;
    }

    public AuditContextBuilder setAccessSourceIp(String accessSourceIp) {
        this.accessSourceIp = accessSourceIp;
        return this;
    }

    public AuditContextBuilder setAccessUserAgent(String accessUserAgent) {
        this.accessUserAgent = accessUserAgent;
        return this;
    }

    public AuditContextBuilder setHttpRequest(AuditHttpRequest httpRequest) {
        this.httpRequest = httpRequest;
        return this;
    }

    public AuditContextBuilder setSubActionIds(List<String> subActionIds) {
        this.subActionIds = subActionIds;
        return this;
    }

    public AuditContextBuilder setSystemId(String systemId) {
        this.systemId = systemId;
        return this;
    }

    public AuditContext build() {
        return new SdkAuditContext(actionId, requestId, username, userIdentifyType, userIdentifyTenantId,
                systemId, bkAppCode, accessType, accessSourceIp, accessUserAgent, httpRequest, subActionIds);
    }
}
