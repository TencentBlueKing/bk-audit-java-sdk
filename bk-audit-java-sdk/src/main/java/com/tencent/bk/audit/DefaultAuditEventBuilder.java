package com.tencent.bk.audit;

import com.tencent.bk.audit.constants.AuditAttributeNames;
import com.tencent.bk.audit.constants.Constants;
import com.tencent.bk.audit.context.ActionAuditContext;
import com.tencent.bk.audit.model.AuditEvent;
import com.tencent.bk.audit.utils.EventIdGenerator;
import com.tencent.bk.audit.utils.VariableResolver;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 默认的审计事件生成Builder
 */
public class DefaultAuditEventBuilder implements AuditEventBuilder {
    private final ActionAuditContext actionAuditContext;

    public DefaultAuditEventBuilder() {
        this.actionAuditContext = ActionAuditContext.current();
    }

    @Override
    public List<AuditEvent> build() {
        List<AuditEvent> events = new ArrayList<>();

        List<String> instanceIdList = actionAuditContext.getInstanceIdList();
        if (CollectionUtils.isNotEmpty(instanceIdList)) {
            List<String> instanceNameList = actionAuditContext.getInstanceNameList();
            List<Object> originInstanceList = actionAuditContext.getOriginInstanceList();
            List<Object> instanceList = actionAuditContext.getInstanceList();

            for (int index = 0; index < instanceIdList.size(); index++) {
                String instanceId = safeGetElement(instanceIdList, index);
                String instanceName = safeGetElement(instanceNameList, index);
                Object originInstance = safeGetElement(originInstanceList, index);
                Object instance = safeGetElement(instanceList, index);
                Map<String, Object> attributes = buildMergedEventAttributes(instanceId, instanceName);
                AuditEvent auditEvent = buildAuditEvent(instanceId, instanceName, originInstance, instance,
                        attributes);
                events.add(auditEvent);
            }
        } else {
            AuditEvent auditEvent = buildAuditEvent(null, null, null, null,
                    actionAuditContext.getAttributes());
            events.add(auditEvent);
        }
        return events;
    }

    private Map<String, Object> buildMergedEventAttributes(String instanceId,
                                                           String instanceName) {
        Map<String, Object> attributeMap = new HashMap<>();
        attributeMap.put(AuditAttributeNames.INSTANCE_ID, instanceId);
        attributeMap.put(AuditAttributeNames.INSTANCE_NAME, instanceName);

        if (MapUtils.isNotEmpty(actionAuditContext.getAttributes())) {
            attributeMap.putAll(actionAuditContext.getAttributes());
        }
        return attributeMap;
    }

    protected <T> T safeGetElement(List<T> list, int index) {
        return list != null && list.size() > index ? list.get(index) : null;
    }

    protected AuditEvent buildAuditEvent(String instanceId,
                                         String instanceName,
                                         Object originInstance,
                                         Object instance,
                                         Map<String, Object> attributes) {
        AuditEvent auditEvent = buildBasicAuditEvent();

        // 审计记录 - 原始数据
        auditEvent.setInstanceOriginData(originInstance);
        // 审计记录 - 更新后数据
        auditEvent.setInstanceData(instance);

        auditEvent.setInstanceId(instanceId);
        auditEvent.setInstanceName(instanceName);
        auditEvent.setContent(resolveAttributes(actionAuditContext.getContent(), attributes));
        return auditEvent;
    }

    protected AuditEvent buildBasicAuditEvent() {
        AuditEvent auditEvent = new AuditEvent();
        auditEvent.setId(EventIdGenerator.generateId());
        auditEvent.setActionId(actionAuditContext.getActionId());
        auditEvent.setResourceTypeId(actionAuditContext.getResourceType());
        auditEvent.setStartTime(actionAuditContext.getStartTime());
        auditEvent.setEndTime(actionAuditContext.getEndTime());
        auditEvent.setResultCode(Constants.RESULT_CODE_SUCCESS);
        auditEvent.setResultContent("Success");
        return auditEvent;
    }

    /**
     * 根据事件属性解析内容
     *
     * @param contentTemplate 内容模板
     * @param eventAttributes 事件属性
     * @return 解析之后的值
     */
    protected String resolveAttributes(String contentTemplate, Map<String, Object> eventAttributes) {
        if (MapUtils.isEmpty(eventAttributes)) {
            return contentTemplate;
        }
        Map<String, String> vars = new HashMap<>();
        eventAttributes.forEach((k, v) -> vars.put(k, v == null ? "" : v.toString()));
        return VariableResolver.resolveVariables(contentTemplate, vars);
    }
}
