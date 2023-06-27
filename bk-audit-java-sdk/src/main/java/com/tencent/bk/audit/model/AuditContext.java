package com.tencent.bk.audit.model;

import com.tencent.bk.audit.GlobalAuditRegistry;

import java.util.List;

/**
 * 审计上下文
 */
public interface AuditContext {
    /**
     * 非法的审计上下文，用于当前审计上下文不存在时返回这个实例（避免返回null导致系统异常)
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
        return GlobalAuditRegistry.get().currentAuditContext();
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
     * 增加操作审计上下文
     *
     * @param actionAuditContext 操作审计上下文
     */
    void addActionAuditContext(ActionAuditContext actionAuditContext);

    /**
     * 结束审计上下文；在所有的操作结束后调用
     */
    void end();

    /**
     * 操作错误时记录错误信息
     *
     * @param resultCode 操作结果
     * @param resultDesc 操作结果描述
     */
    void error(int resultCode, String resultDesc);

    /**
     * 获取审计事件
     *
     * @return 审计事件
     */
    List<AuditEvent> getEvents();
}
