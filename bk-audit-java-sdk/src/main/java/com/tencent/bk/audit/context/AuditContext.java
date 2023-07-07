package com.tencent.bk.audit.context;

import com.tencent.bk.audit.constants.AccessTypeEnum;
import com.tencent.bk.audit.constants.UserIdentifyTypeEnum;
import com.tencent.bk.audit.model.AuditEvent;
import com.tencent.bk.audit.model.AuditHttpRequest;

import java.util.List;

/**
 * 审计上下文
 */
public interface AuditContext {
    /**
     * 非法的审计上下文
     */
    AuditContext INVALID = new InvalidAuditContext();

    static AuditContextBuilder builder(String actionId) {
        return AuditContextBuilder.builder(actionId);
    }

    /**
     * 返回当前审计上下文
     *
     * @return 当前审计上下文
     */
    static AuditContext current() {
        AuditContext auditContext = LazyAuditContextHolder.get().current();
        return auditContext != null ? auditContext : AuditContext.INVALID;
    }

    /**
     * 更新操作ID
     *
     * @param actionId 操作ID
     */
    void updateActionId(String actionId);

    /**
     * 设置当前操作审计上下文
     *
     * @param actionAuditContext 操作审计上下文
     */
    void setCurrentActionAuditContext(ActionAuditContext actionAuditContext);

    /**
     * 获取当前操作审计上下文
     *
     * @return 当前操作审计上下文
     */
    ActionAuditContext currentActionAuditContext();

    /**
     * 新增操作审计上下文
     *
     * @param actionAuditContext 操作审计上下文
     */
    void addActionAuditContext(ActionAuditContext actionAuditContext);

    /**
     * 结束审计上下文
     */
    void end();

    /**
     * 操作错误时记录错误信息
     *
     * @param resultCode 操作结果，对应审计事件中的 result_code
     * @param resultDesc 操作结果描述，对应审计事件中的 result_content
     */
    void error(int resultCode, String resultDesc);

    /**
     * 获取上下文包含的审计事件
     *
     * @return 审计事件
     */
    List<AuditEvent> getEvents();

    String getRequestId();

    String getUsername();

    UserIdentifyTypeEnum getUserIdentifyType();

    String getUserIdentifyTenantId();

    Long getStartTime();

    Long getEndTime();

    String getBkAppCode();

    AccessTypeEnum getAccessType();

    String getAccessSourceIp();

    String getAccessUserAgent();

    String getActionId();

    List<String> getSubActionIds();

    AuditHttpRequest getHttpRequest();
}
