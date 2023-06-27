package com.tencent.bk.audit;

import com.tencent.bk.audit.constants.AccessTypeEnum;
import com.tencent.bk.audit.constants.UserIdentifyTypeEnum;

import javax.servlet.http.HttpServletRequest;

public interface AuditRequestProvider {
    HttpServletRequest getRequest();

    String getUsername();

    UserIdentifyTypeEnum getUserIdentifyType();

    default String getUserIdentifyTenantId() {
        return null;
    }

    AccessTypeEnum getAccessType();

    default String getRequestId() {
        return null;
    }

    default String getBkAppCode() {
        return null;
    }

    String getClientIp();

    String getUserAgent();
}
