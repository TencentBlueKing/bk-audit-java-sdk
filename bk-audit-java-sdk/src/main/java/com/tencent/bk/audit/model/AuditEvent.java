package com.tencent.bk.audit.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tencent.bk.audit.constants.AccessTypeEnum;
import com.tencent.bk.audit.constants.AuditEventKey;
import com.tencent.bk.audit.constants.Constants;
import com.tencent.bk.audit.constants.UserIdentifyTypeEnum;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

import static com.tencent.bk.audit.constants.Constants.RESULT_CODE_SUCCESS;
import static com.tencent.bk.audit.constants.Constants.RESULT_SUCCESS_DESC;

/**
 * 审计事件标准模型，用于定义上报给审计中心的事件数据
 */
@Data
public class AuditEvent {

    /**
     * 事件ID
     */
    @JsonProperty("event_id")
    private String id;

    /**
     * 事件描述
     */
    @JsonProperty("event_content")
    private String content;

    /**
     * 请求ID
     */
    @JsonProperty("request_id")
    private String requestId;

    /**
     * 操作人用户名
     */
    @JsonProperty("username")
    private String username;

    /**
     * 操作人账号类型
     */
    @JsonProperty("user_identify_type")
    private int userIdentifyType = UserIdentifyTypeEnum.UNKNOWN.getValue();

    /**
     * 操作人租户ID
     */
    @JsonProperty("user_identify_tenant_id")
    private String userIdentifyTenantId;

    /**
     * 事件开始时间,UTC时间，精确到毫秒
     */
    @JsonProperty("start_time")
    private Long startTime;

    /**
     * 事件结束时间,UTC时间，精确到毫秒
     */
    @JsonProperty("end_time")
    private Long endTime;

    /**
     * 事件上报模块
     */
    @JsonProperty("bk_app_code")
    private String bkAppCode;

    /**
     * 访问方式
     */
    @JsonProperty("access_type")
    private int accessType = AccessTypeEnum.OTHER.getValue();

    /**
     * 访问来源IP地址
     */
    @JsonProperty("access_source_ip")
    private String accessSourceIp;

    /**
     * 访问客户端类型
     */
    @JsonProperty("access_user_agent")
    private String accessUserAgent;

    /**
     * 操作ID
     */
    @JsonProperty("action_id")
    private String actionId;

    /**
     * 资源类型ID
     */
    @JsonProperty("resource_type_id")
    private String resourceTypeId;

    /**
     * 资源实例ID
     */
    @JsonProperty("instance_id")
    private String instanceId;

    /**
     * 资源实例名称
     */
    @JsonProperty("instance_name")
    private String instanceName;

    /**
     * 资源实例数据。必须符合在审计中心定义的资源Schema
     */
    @JsonProperty("instance_data")
    private Object instanceData;

    /**
     * 资源实例原始数据。必须符合在审计中心定义的资源Schema。对于更新/删除操作，建议上报
     */
    @JsonProperty("instance_origin_data")
    private Object instanceOriginData;

    /**
     * 操作结果
     */
    @JsonProperty("result_code")
    private int resultCode = RESULT_CODE_SUCCESS;

    /**
     * 操作结果描述
     */
    @JsonProperty("result_content")
    private String resultContent = RESULT_SUCCESS_DESC;

    /**
     * 拓展信息，各个系统可以根据具体需要扩展增加上报数据字段
     */
    @JsonProperty("extend_data")
    private Map<String, Object> extendData;

    @JsonProperty("audit_event_signature")
    private String auditEventSignature = Constants.AUDIT_EVENT_SIGNATURE;

    public AuditEvent() {
    }

    public AuditEvent(String actionId) {
        this.actionId = actionId;
    }

    /**
     * 添加扩展数据
     */
    public void addExtendData(String key, Object value) {
        if (extendData == null) {
            extendData = new HashMap<>();
        }
        extendData.put(key, value);
    }

    public AuditEventKey toAuditKey() {
        return AuditEventKey.build(actionId, resourceTypeId, instanceId);
    }


}
