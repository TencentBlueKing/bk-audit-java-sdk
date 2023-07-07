package com.tencent.bk.audit;

import com.tencent.bk.audit.constants.AccessTypeEnum;
import com.tencent.bk.audit.constants.UserIdentifyTypeEnum;
import com.tencent.bk.audit.model.AuditHttpRequest;

/**
 * 操作请求 Provider
 */
public interface AuditRequestProvider {
    /**
     * 获取操作对应的 HttpRequest
     */
    AuditHttpRequest getRequest();

    /**
     * 获取操作者
     */
    String getUsername();

    /**
     * 获取操作账号类型
     */
    UserIdentifyTypeEnum getUserIdentifyType();

    /**
     * 获取操作人租户ID （暂不支持)
     */
    default String getUserIdentifyTenantId() {
        return null;
    }

    /**
     * 获取访问方式
     */
    AccessTypeEnum getAccessType();

    /**
     * 获取请求 ID，可以是 TraceId。用于审计事件之间的关联
     */
    default String getRequestId() {
        return null;
    }

    /**
     * 获取操作发起的蓝鲸应用 App Code
     */
    default String getBkAppCode() {
        return null;
    }

    /**
     * 获取操作启发的客户端 IP
     */
    String getClientIp();

    /**
     * 获取访问客户端类型
     */
    String getUserAgent();
}
