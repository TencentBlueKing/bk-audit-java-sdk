package com.tencent.bk.audit.exception;

/**
 * 审计 SDK 异常
 */
public class AuditException extends RuntimeException {
    public AuditException(String message) {
        super(message);
    }

    public AuditException(String message, Throwable cause) {
        super(message, cause);
    }
}
