package com.tencent.bk.audit.utils;

import java.util.UUID;

/**
 * 审计事件ID生成
 */
public class EventIdGenerator {
    /**
     * 生成审计事件UUID
     *
     * @return UUID
     */
    public static String generateId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
