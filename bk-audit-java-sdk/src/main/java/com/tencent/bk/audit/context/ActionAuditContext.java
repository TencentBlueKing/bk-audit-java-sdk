package com.tencent.bk.audit.context;

import com.tencent.bk.audit.AuditCallable;
import com.tencent.bk.audit.AuditRunnable;
import com.tencent.bk.audit.model.AuditEvent;

import java.util.List;
import java.util.Map;

/**
 * 操作审计上下文
 */
public interface ActionAuditContext {
    /**
     * 非法的操作审计上下文
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

    default <T> AuditCallable<T> wrapActionCallable(AuditCallable<T> callable) {
        return () -> {
            ActionAuditScope scope = makeCurrent();
            try {
                return callable.call();
            } finally {
                safelyEndActionAuditContext(scope);
            }
        };
    }

    default void safelyEndActionAuditContext(ActionAuditScope scope) {
        end();
        if (scope != null) {
            scope.close();
        }
    }

    default AuditRunnable wrapActionRunnable(AuditRunnable auditRunnable) {
        return () -> {
            ActionAuditScope scope = makeCurrent();
            try {
                auditRunnable.run();
            } finally {
                safelyEndActionAuditContext(scope);
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

    /**
     * 设置为 disabled 状态，该操作上下文将不会生效和产生审计事件
     */
    ActionAuditContext disable();

    /**
     * 操作上下文是否被废弃
     */
    boolean isDisabled();

    /**
     * 添加扩展数据
     */
    ActionAuditContext addExtendData(String key, Object value);

    Map<String, Object> getExtendData();

    Object getExtendDataValue(String key);

}
