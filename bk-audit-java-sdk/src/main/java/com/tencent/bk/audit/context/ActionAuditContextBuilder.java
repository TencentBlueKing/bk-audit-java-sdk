package com.tencent.bk.audit.context;

import com.tencent.bk.audit.AuditEventBuilder;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActionAuditContextBuilder {
    /**
     * 操作 ID
     */
    private final String actionId;

    /**
     * 资源类型
     */
    private String resourceType;

    /**
     * 审计事件描述
     */
    private String content;

    public Class<? extends AuditEventBuilder> eventBuilder;

    /**
     * 操作实例ID列表
     */
    private List<String> instanceIdList;

    /**
     * 操作实例名称列表，需要与instanceIdList中的ID一一对应
     */
    private List<String> instanceNameList;

    /**
     * 原始实例列表
     */
    private List<Object> originInstanceList;

    /**
     * 当前实例列表
     */
    private List<Object> instanceList;

    /**
     * 其它通过 ActionAuditRecord.AuditAttribute 设置的属性
     */
    private Map<String, Object> attributes = new HashMap<>();

    /**
     * 管理空间类型（比如 project/biz等）
     */
    private String scopeType;

    /**
     * 管理空间ID（比如项目ID、cmdb业务ID）
     */
    private String scopeId;

    public static ActionAuditContextBuilder builder(String actionId) {
        return new ActionAuditContextBuilder(actionId);
    }


    private ActionAuditContextBuilder(String actionId) {
        this.actionId = actionId;
    }

    public ActionAuditContextBuilder setResourceType(String resourceType) {
        this.resourceType = resourceType;
        return this;
    }

    public ActionAuditContextBuilder setContent(String content) {
        this.content = content;
        return this;
    }

    public ActionAuditContextBuilder setEventBuilder(Class<? extends AuditEventBuilder> eventBuilder) {
        this.eventBuilder = eventBuilder;
        return this;
    }

    public ActionAuditContextBuilder setInstanceIdList(List<String> instanceIdList) {
        this.instanceIdList = instanceIdList;
        return this;
    }

    public ActionAuditContextBuilder setInstanceNameList(List<String> instanceNameList) {
        this.instanceNameList = instanceNameList;
        return this;
    }

    public ActionAuditContextBuilder setOriginInstanceList(List<Object> originInstanceList) {
        this.originInstanceList = originInstanceList;
        return this;
    }

    public ActionAuditContextBuilder setInstanceList(List<Object> instanceList) {
        this.instanceList = instanceList;
        return this;
    }

    public ActionAuditContextBuilder setInstanceId(String instanceId) {
        this.instanceIdList = Collections.singletonList(instanceId);
        return this;
    }

    public ActionAuditContextBuilder setInstanceName(String instanceName) {
        this.instanceNameList = Collections.singletonList(instanceName);
        return this;
    }

    public ActionAuditContextBuilder setOriginInstanceList(Object originInstance) {
        this.originInstanceList = Collections.singletonList(originInstance);
        return this;
    }

    public ActionAuditContextBuilder setInstance(Object instance) {
        this.instanceList = Collections.singletonList(instance);
        return this;
    }

    public ActionAuditContextBuilder setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
        return this;
    }

    public ActionAuditContextBuilder addAttribute(String attrName, Object attrValue) {
        if (this.attributes == null) {
            this.attributes = new HashMap<>();
        }
        this.attributes.put(attrName, attrValue);
        return this;
    }

    public ActionAuditContextBuilder setScopeType(String scopeType) {
        this.scopeType = scopeType;
        return this;
    }

    public ActionAuditContextBuilder setScopeId(String scopeId) {
        this.scopeId = scopeId;
        return this;
    }

    public ActionAuditContext build() {
        return new SdkActionAuditContext(actionId, resourceType, instanceIdList, instanceNameList,
                originInstanceList, instanceList, content, eventBuilder, attributes, scopeType, scopeId);
    }
}
