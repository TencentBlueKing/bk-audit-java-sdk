package com.tencent.bk.audit.constants;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * 审计事件KEY
 * <p>
 * 在一个审计上下文中, 操作+资源类型+资源id 即可唯一确定审计事件
 */
public class AuditEventKey {
    private final String actionId;
    private final String resourceType;
    private final String resourceId;

    private AuditEventKey(String actionId, String resourceType, String resourceId) {
        this.actionId = actionId;
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }

    public static AuditEventKey build(String actionId, String resourceType, String resourceId) {
        return new AuditEventKey(actionId, resourceType, resourceId);
    }

    public static AuditEventKey build(String actionId) {
        return new AuditEventKey(actionId, null, null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuditEventKey auditEventKey = (AuditEventKey) o;
        return Objects.equals(actionId, auditEventKey.actionId) &&
                Objects.equals(resourceType, auditEventKey.resourceType) &&
                Objects.equals(resourceId, auditEventKey.resourceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(actionId, resourceType, resourceId);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(actionId);
        if (StringUtils.isNotBlank(resourceType)) {
            sb.append(":").append(resourceType);
        }
        if (StringUtils.isNotBlank(resourceId)) {
            sb.append(":").append(resourceId);
        }
        return sb.toString();
    }
}
