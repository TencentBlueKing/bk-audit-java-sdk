package com.tencent.bk.audit.model;

import com.tencent.bk.audit.ActionCallable;
import com.tencent.bk.audit.ActionRunnable;

import java.util.List;
import java.util.Map;

/**
 * 操作审计上下文实现
 */
public interface ActionAuditContext {
    /**
     * 非法的操作审计上下文，用于当前操作审计上下文不存在时返回这个实例（避免返回null导致系统异常)
     */
    ActionAuditContext INVALID = new InvalidActionAuditContext();

    static ActionAuditContextBuilder builder(String actionId) {
        return ActionAuditContextBuilder.builder(actionId);
    }

    /**
     * 返回当前操作审计上下文
     */
    static ActionAuditContext current() {
        return AuditContext.current().currentActionAuditContext();
    }

    /**
     * 设置自身为当前操作审计上下文
     *
     * @return scope
     */
    ActionAuditScope makeCurrent();

    /**
     * 结束操作审计上下文
     */
    void end();

    default <T> ActionCallable<T> wrapActionCallable(ActionCallable<T> callable) {
        return () -> {
            ActionAuditScope scope = null;
            ActionAuditContext current = null;
            try {
                scope = makeCurrent();
                current = current();
            } catch (Throwable ignore) {
                // 保证业务代码正常执行，忽略所有审计错误
            }
            try {
                return callable.call();
            } finally {
                safelyEndActionAuditContext(scope, current);
            }
        };
    }

    default void safelyEndActionAuditContext(ActionAuditScope scope,
                                             ActionAuditContext current) {
        try {
            if (current != null) {
                current.end();
            }
            if (scope != null) {
                scope.close();
            }
        } catch (Throwable ignore) {
            // 保证业务代码正常执行，忽略所有审计错误
        }
    }

    default ActionRunnable wrapActionRunnable(ActionRunnable actionRunnable) {
        return () -> {
            ActionAuditScope scope = null;
            ActionAuditContext current = null;
            try {
                scope = makeCurrent();
                current = current();
            } catch (Throwable ignore) {
                // 保证业务代码正常执行，忽略所有审计错误
            }
            try {
                actionRunnable.run();
            } finally {
                safelyEndActionAuditContext(scope, current);
            }
        };
    }

    /**
     * 获取审计事件
     *
     * @return 生成的审计事件
     */
    List<AuditEvent> getEvents();

    /**
     * 新增属性
     *
     * @param name  属性名称
     * @param value 属性值
     */
    ActionAuditContext addAttribute(String name, Object value);

    String getActionId();

    Long getStartTime();

    Long getEndTime();

    String getResourceType();

    List<String> getInstanceIdList();

    List<String> getInstanceNameList();

    List<Object> getOriginInstanceList();

    List<Object> getInstanceList();

    String getContent();

    Map<String, Object> getAttributes();

    ActionAuditContext setInstanceIdList(List<String> instanceIdList);

    ActionAuditContext setInstanceNameList(List<String> instanceNameList);

    ActionAuditContext setOriginInstanceList(List<Object> originInstanceList);

    ActionAuditContext setInstanceList(List<Object> instanceList);

    ActionAuditContext setInstanceId(String instanceId);

    ActionAuditContext setInstanceName(String instanceName);

    ActionAuditContext setOriginInstance(Object originInstance);

    ActionAuditContext setInstance(Object instance);

    ActionAuditContext addInstanceInfo(String instanceId, String instanceName, Object originInstance, Object instance);
}
