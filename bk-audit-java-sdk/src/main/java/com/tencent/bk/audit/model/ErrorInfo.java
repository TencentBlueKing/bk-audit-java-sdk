package com.tencent.bk.audit.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 操作错误信息
 */
@Data
@NoArgsConstructor
public class ErrorInfo {
    /**
     * 错误码。对应 AuditEvent.resultCode
     *
     * @see AuditEvent
     */
    private Integer errorCode;
    /**
     * 错误描述。对应 AuditEvent.resultContent
     *
     * @see AuditEvent
     */
    private String errorMessage;

    public ErrorInfo(Integer errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
