package com.tencent.bk.audit.example;

import com.tencent.bk.audit.AuditClient;
import com.tencent.bk.audit.constants.AccessTypeEnum;
import com.tencent.bk.audit.constants.UserIdentifyTypeEnum;
import com.tencent.bk.audit.context.ActionAuditContext;
import com.tencent.bk.audit.context.AuditContext;

import static com.tencent.bk.audit.constants.AuditAttributeNames.INSTANCE_ID;
import static com.tencent.bk.audit.constants.AuditAttributeNames.INSTANCE_NAME;

/**
 * 一次操作（请求）产生一个审计事件
 */
public class SingleAuditEventExample {
    private final AuditClient auditClient;

    public SingleAuditEventExample(AuditClient auditClient) {
        this.auditClient = auditClient;
    }

    public void run() {
        // 构造审计上下文
        AuditContext auditContext = auditClient.auditContextBuilder("execute_job_plan")
                .setSystemId("bk_job")
                .setRequestId("3a84858499bd71d674bc40d4f73cb41a")
                .setAccessSourceIp("127.0.0.1")
                .setAccessType(AccessTypeEnum.CONSOLE)
                .setAccessUserAgent("Chrome")
                .setUserIdentifyType(UserIdentifyTypeEnum.PERSONAL)
                .setUsername("admin")
                .build();
        // 对操作进行审计
        auditClient.audit(auditContext, this::action);
    }

    private void action() {
        // 使用 ActionAuditContext 封装 Action 代码，自动封装审计逻辑
        ActionAuditContext.builder("execute_job_plan")
                .setResourceType("job_plan")
                .setInstanceId("1000")
                .setInstanceName("test_audit_execute_job_plan")
                .setContent("Execute job plan [{{" + INSTANCE_NAME + "}}]({{" + INSTANCE_ID + "}})")
                .build()
                .wrapActionRunnable(() -> {
                    // action code
                    ActionAuditContext.current().addAttribute("host_id", "1,2,3,4");
                })
                .run();
    }
}
