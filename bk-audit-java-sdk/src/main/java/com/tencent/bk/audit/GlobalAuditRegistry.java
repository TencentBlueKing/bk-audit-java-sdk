package com.tencent.bk.audit;

public class GlobalAuditRegistry {
    private static volatile Audit audit = null;

    public static void register(Audit audit) {
        GlobalAuditRegistry.audit = audit;
    }

    public static Audit get() {
        return audit;
    }
}
