package com.tencent.bk.audit.context;

import com.tencent.bk.audit.constants.AccessTypeEnum;
import com.tencent.bk.audit.constants.AuditEventKey;
import com.tencent.bk.audit.constants.Constants;
import com.tencent.bk.audit.constants.UserIdentifyTypeEnum;
import com.tencent.bk.audit.model.AuditEvent;
import com.tencent.bk.audit.model.AuditHttpRequest;
import com.tencent.bk.audit.utils.EventIdGenerator;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * 审计上下文实现
 */
public class SdkAuditContext implements AuditContext {

    private final String requestId;

    private final String username;

    private final UserIdentifyTypeEnum userIdentifyType;

    private final String userIdentifyTenantId;

    private final Long startTime;

    private Long endTime;

    private final String bkAppCode;

    private final AccessTypeEnum accessType;

    private final String accessSourceIp;

    private final String accessUserAgent;

    /**
     * 操作ID
     */
    private String actionId;

    /**
     * 子操作ID
     */
    private final List<String> subActionIds;

    private final AuditHttpRequest httpRequest;

    /**
     * 包含的操作审计上下文
     */
    private final List<ActionAuditContext> actionAuditContexts = new ArrayList<>();

    /**
     * 审计事件列表
     */
    private final List<AuditEvent> events = new ArrayList<>();

    /**
     * 当前操作审计上下文
     */
    private ActionAuditContext currentActionAuditContext;

    private Integer resultCode;

    private String resultContent;


    SdkAuditContext(String actionId,
                    String requestId,
                    String username,
                    UserIdentifyTypeEnum userIdentifyType,
                    String userIdentifyTenantId,
                    String bkAppCode,
                    AccessTypeEnum accessType,
                    String accessSourceIp,
                    String accessUserAgent,
                    AuditHttpRequest httpRequest,
                    List<String> subActionIds) {
        this.actionId = actionId;
        this.requestId = requestId;
        this.username = username;
        this.userIdentifyType = userIdentifyType;
        this.userIdentifyTenantId = userIdentifyTenantId;
        this.startTime = System.currentTimeMillis();
        this.bkAppCode = bkAppCode;
        this.accessType = accessType;
        this.accessSourceIp = accessSourceIp;
        this.accessUserAgent = accessUserAgent;
        this.httpRequest = httpRequest;
        this.subActionIds = subActionIds == null ? Collections.emptyList() : subActionIds;
    }

    @Override
    public ActionAuditContext currentActionAuditContext() {
        return currentActionAuditContext != null ? currentActionAuditContext : ActionAuditContext.INVALID;
    }

    @Override
    public void addActionAuditContext(ActionAuditContext actionAuditContext) {
        actionAuditContexts.add(actionAuditContext);
    }

    @Override
    public void end() {
        this.endTime = System.currentTimeMillis();
        buildAuditEvents();
    }

    private void buildAuditEvents() {
        if (StringUtils.isEmpty(actionId)) {
            return;
        }
        Map<AuditEventKey, AuditEvent> auditEvents = new HashMap<>();
        if (isFail()) {
            AuditEvent auditEvent = new AuditEvent(actionId);
            auditEvent.setStartTime(startTime);
            auditEvent.setEndTime(endTime);
            auditEvent.setResultCode(resultCode);
            auditEvent.setResultContent(resultContent);
            auditEvents.put(auditEvent.toAuditKey(), auditEvent);
        } else {
            actionAuditContexts.stream()
                    .filter(actionAuditContext -> isActionRecordable(actionAuditContext.getActionId()))
                    .forEach(actionAuditContext ->
                            actionAuditContext.getEvents().forEach(
                                    auditEvent -> auditEvents.put(auditEvent.toAuditKey(), auditEvent)
                            )
                    );
        }

        if (!auditEvents.isEmpty()) {
            auditEvents.values().forEach(this::addContextAttributes);
            this.events.addAll(auditEvents.values());
        }
    }

    private boolean isFail() {
        return this.resultCode != null && this.resultCode != Constants.RESULT_CODE_SUCCESS;
    }

    private void addContextAttributes(AuditEvent auditEvent) {
        auditEvent.setRequestId(requestId);
        auditEvent.setBkAppCode(bkAppCode);

        if (StringUtils.isEmpty(auditEvent.getId())) {
            auditEvent.setId(EventIdGenerator.generateId());
        }
        if (accessType != null) {
            auditEvent.setAccessType(accessType.getValue());
        }
        auditEvent.setAccessUserAgent(accessUserAgent);
        auditEvent.setAccessSourceIp(accessSourceIp);

        auditEvent.setUsername(username);
        if (userIdentifyType != null) {
            auditEvent.setUserIdentifyType(UserIdentifyTypeEnum.PERSONAL.getValue());
        }
        auditEvent.setUserIdentifyTenantId(userIdentifyTenantId);

        if (httpRequest != null) {
            auditEvent.addAuditHttpRequestExtendData(httpRequest.toAuditHttpRequestData());
        }
    }

    private boolean isActionRecordable(String actionId) {
        return StringUtils.isNotEmpty(this.actionId) &&
                (this.actionId.equals(actionId) || this.subActionIds.contains(actionId));
    }

    @Override
    public void setCurrentActionAuditContext(ActionAuditContext actionAuditContext) {
        this.currentActionAuditContext = actionAuditContext;
    }

    @Override
    public void error(int resultCode, String resultDesc) {
        this.resultCode = resultCode;
        this.resultContent = StringUtils.isNotBlank(resultDesc) ? resultDesc : "Fail";
    }

    @Override
    public List<AuditEvent> getEvents() {
        return Collections.unmodifiableList(events);
    }

    @Override
    public void updateActionId(String actionId) {
        if (StringUtils.isEmpty(this.actionId)) {
            this.actionId = actionId;
        }
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", SdkAuditContext.class.getSimpleName() + "[", "]")
                .add("requestId='" + requestId + "'")
                .add("username='" + username + "'")
                .add("userIdentifyType=" + userIdentifyType)
                .add("userIdentifyTenantId='" + userIdentifyTenantId + "'")
                .add("startTime=" + startTime)
                .add("endTime=" + endTime)
                .add("bkAppCode='" + bkAppCode + "'")
                .add("accessType=" + accessType)
                .add("accessSourceIp='" + accessSourceIp + "'")
                .add("accessUserAgent='" + accessUserAgent + "'")
                .add("actionId='" + actionId + "'")
                .add("httpRequest=" + httpRequest)
                .add("actionAuditContexts=" + actionAuditContexts)
                .add("events=" + events)
                .add("currentActionAuditContext=" + currentActionAuditContext)
                .add("subActionIds=" + subActionIds)
                .toString();
    }

    @Override
    public String getRequestId() {
        return requestId;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public UserIdentifyTypeEnum getUserIdentifyType() {
        return userIdentifyType;
    }

    @Override
    public String getUserIdentifyTenantId() {
        return userIdentifyTenantId;
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
    public String getBkAppCode() {
        return bkAppCode;
    }

    @Override
    public AccessTypeEnum getAccessType() {
        return accessType;
    }

    @Override
    public String getAccessSourceIp() {
        return accessSourceIp;
    }

    @Override
    public String getAccessUserAgent() {
        return accessUserAgent;
    }

    @Override
    public String getActionId() {
        return actionId;
    }

    @Override
    public List<String> getSubActionIds() {
        return subActionIds;
    }

    @Override
    public AuditHttpRequest getHttpRequest() {
        return httpRequest;
    }
}
