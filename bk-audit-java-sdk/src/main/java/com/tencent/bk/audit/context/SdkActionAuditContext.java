package com.tencent.bk.audit.context;

import com.tencent.bk.audit.AuditEventBuilder;
import com.tencent.bk.audit.DefaultAuditEventBuilder;
import com.tencent.bk.audit.exception.AuditException;
import com.tencent.bk.audit.model.AuditEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * 操作审计上下文
 */
@Slf4j
public class SdkActionAuditContext implements ActionAuditContext {

    /**
     * 操作 ID
     */
    private final String actionId;

    /**
     * 操作开始时间
     */
    private final Long startTime;

    /**
     * 操作结束时间
     */
    private Long endTime;

    /**
     * 资源类型
     */
    private final String resourceType;

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
     * 审计事件描述
     */
    private final String content;

    private final Class<? extends AuditEventBuilder> eventBuilderClass;

    /**
     * 其它通过 ActionAuditRecord.AuditAttribute 设置的属性
     */
    private final Map<String, Object> attributes;

    /**
     * 当前操作产生的审计事件列表
     */
    private final List<AuditEvent> events = new ArrayList<>();

    SdkActionAuditContext(String actionId,
                          String resourceType,
                          List<String> instanceIdList,
                          List<String> instanceNameList,
                          List<Object> originInstanceList,
                          List<Object> instanceList,
                          String content,
                          Class<? extends AuditEventBuilder> eventBuilderClass,
                          Map<String, Object> attributes) {
        this.actionId = actionId;
        this.startTime = System.currentTimeMillis();
        this.resourceType = resourceType;
        this.instanceIdList = instanceIdList;
        this.instanceNameList = instanceNameList;
        this.originInstanceList = originInstanceList;
        this.instanceList = instanceList;
        this.content = content;
        this.eventBuilderClass = eventBuilderClass == null ? DefaultAuditEventBuilder.class : eventBuilderClass;
        this.attributes = (attributes == null ? new HashMap<>() : attributes);
    }

    @Override
    public ActionAuditScope makeCurrent() {
        ActionAuditContext beforeContext = AuditContext.current().currentActionAuditContext();
        AuditContext.current().setCurrentActionAuditContext(this);
        return new ActionAuditScopeImpl(beforeContext, this);
    }

    @Override
    public ActionAuditContext addAttribute(String name, Object value) {
        attributes.put(name, value);
        return this;
    }

    @Override
    public void end() {
        endTime = System.currentTimeMillis();
        buildEvents();
        AuditContext.current().addActionAuditContext(this);
    }

    private void buildEvents() {
        try {
            AuditEventBuilder eventBuilder = eventBuilderClass.newInstance();
            events.addAll(eventBuilder.build());
        } catch (Throwable e) {
            log.error("ActionAuditContext - build event caught error", e);
            throw new AuditException("Build audit event error", e);
        }
    }

    @Override
    public String getActionId() {
        return actionId;
    }

    @Override
    public Long getStartTime() {
        return startTime;
    }

    @Override
    public Long getEndTime() {
        return endTime;
    }

    @Override
    public String getResourceType() {
        return resourceType;
    }

    @Override
    public List<String> getInstanceIdList() {
        return instanceIdList == null ? null : Collections.unmodifiableList(instanceIdList);
    }

    @Override
    public List<String> getInstanceNameList() {
        return instanceNameList == null ? null : Collections.unmodifiableList(instanceNameList);
    }

    @Override
    public List<Object> getOriginInstanceList() {
        return originInstanceList == null ? null : Collections.unmodifiableList(originInstanceList);
    }

    @Override
    public List<Object> getInstanceList() {
        return instanceList == null ? null : Collections.unmodifiableList(instanceList);
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return Collections.unmodifiableMap(attributes);
    }

    @Override
    public List<AuditEvent> getEvents() {
        return Collections.unmodifiableList(events);
    }

    @Override
    public ActionAuditContext setInstanceIdList(List<String> instanceIdList) {
        this.instanceIdList = instanceIdList;
        return this;
    }

    @Override
    public ActionAuditContext setInstanceNameList(List<String> instanceNameList) {
        this.instanceNameList = instanceNameList;
        return this;
    }

    @Override
    public ActionAuditContext setOriginInstanceList(List<Object> originInstanceList) {
        this.originInstanceList = originInstanceList;
        return this;
    }

    @Override
    public ActionAuditContext setInstanceList(List<Object> instanceList) {
        this.instanceList = instanceList;
        return this;
    }

    @Override
    public ActionAuditContext setInstanceId(String instanceId) {
        this.instanceIdList = new ArrayList<>(1);
        this.instanceIdList.add(instanceId);
        return this;
    }

    @Override
    public ActionAuditContext setInstanceName(String instanceName) {
        this.instanceNameList = new ArrayList<>(1);
        this.instanceNameList.add(instanceName);
        return this;
    }

    @Override
    public ActionAuditContext setOriginInstance(Object originInstance) {
        this.originInstanceList = new ArrayList<>(1);
        this.originInstanceList.add(originInstance);
        return this;
    }

    @Override
    public ActionAuditContext setInstance(Object instance) {
        this.instanceList = new ArrayList<>(1);
        this.instanceList.add(instance);
        return this;
    }

    @Override
    public ActionAuditContext addInstanceInfo(String instanceId,
                                              String instanceName,
                                              Object originInstance,
                                              Object instance) {
        instanceIdList = addInstanceToList(instanceId, instanceIdList);
        instanceNameList = addInstanceToList(instanceName, instanceNameList);
        originInstanceList = addInstanceToList(originInstance, originInstanceList);
        instanceList = addInstanceToList(instance, instanceList);
        return this;
    }

    private <E> List<E> addInstanceToList(E instance, List<E> instanceList) {
        if (instance != null) {
            if (instanceList == null) {
                instanceList = new ArrayList<>();
            }
            instanceList.add(instance);
        }
        return instanceList;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", SdkActionAuditContext.class.getSimpleName() + "[", "]")
                .add("actionId='" + actionId + "'")
                .add("startTime=" + startTime)
                .add("endTime=" + endTime)
                .add("resourceType='" + resourceType + "'")
                .add("instanceIdList=" + instanceIdList)
                .add("instanceNameList=" + instanceNameList)
                .add("originInstanceList=" + originInstanceList)
                .add("instanceList=" + instanceList)
                .add("content='" + content + "'")
                .add("eventBuilderClass=" + eventBuilderClass)
                .add("attributes=" + attributes)
                .add("events=" + events)
                .toString();
    }
}
