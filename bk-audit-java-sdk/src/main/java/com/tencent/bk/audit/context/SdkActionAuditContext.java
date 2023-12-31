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
     * 审计事件拓展信息，各个系统可以根据具体需要扩展增加上报数据字段
     */
    private Map<String, Object> extendData;

    /**
     * 当前操作产生的审计事件列表
     */
    private final List<AuditEvent> events = new ArrayList<>();

    /**
     * 操作上下文是否被废弃
     */
    private boolean disabled;

    /**
     * 管理空间类型（比如 project/biz等）
     */
    private String scopeType;

    /**
     * 管理空间ID（比如项目ID、cmdb业务ID）
     */
    private String scopeId;

    SdkActionAuditContext(String actionId,
                          String resourceType,
                          List<String> instanceIdList,
                          List<String> instanceNameList,
                          List<Object> originInstanceList,
                          List<Object> instanceList,
                          String content,
                          Class<? extends AuditEventBuilder> eventBuilderClass,
                          Map<String, Object> attributes,
                          String scopeType,
                          String scopeId) {
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
        this.scopeType = scopeType;
        this.scopeId = scopeId;
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
    public ActionAuditContext disable() {
        this.disabled = true;
        return this;
    }

    @Override
    public boolean isDisabled() {
        return this.disabled;
    }

    @Override
    public ActionAuditContext addExtendData(String key, Object value) {
        if (extendData == null) {
            extendData = new HashMap<>();
        }
        extendData.put(key, value);
        return this;
    }

    @Override
    public Map<String, Object> getExtendData() {
        return extendData;
    }

    @Override
    public Object getExtendDataValue(String key) {
        return extendData == null ? null : extendData.get(key);
    }

    @Override
    public ActionAuditContext setScopeType(String scopeType) {
        this.scopeType = scopeType;
        return this;
    }

    @Override
    public ActionAuditContext setScopeId(String scopeId) {
        this.scopeId = scopeId;
        return this;
    }

    @Override
    public String getScopeType() {
        return this.scopeType;
    }

    @Override
    public String getScopeId() {
        return this.scopeId;
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
                .add("disabled=" + disabled)
                .add("extendData=" + extendData)
                .add("scopeType=" + scopeType)
                .add("scopeId=" + scopeId)
                .toString();
    }
}
