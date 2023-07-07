package com.tencent.bk.audit;

import com.tencent.bk.audit.exception.AuditException;
import lombok.extern.slf4j.Slf4j;

/**
 * 提供 AuditClient 组件的静态访问方式
 */
@Slf4j
public class GlobalAuditClient {
    private static volatile AuditClient auditClient = null;

    public static void register(AuditClient auditClient) {
        synchronized (GlobalAuditClient.class) {
            if (GlobalAuditClient.auditClient != null) {
                log.error("GlobalAuditClient can not register more than once");
                throw new AuditException("GlobalAuditClient can not register more than once");
            }
            GlobalAuditClient.auditClient = auditClient;
        }
    }

    public static AuditClient get() {
        return auditClient;
    }
}
