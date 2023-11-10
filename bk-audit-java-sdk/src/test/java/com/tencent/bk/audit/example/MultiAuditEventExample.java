package com.tencent.bk.audit.example;

import com.tencent.bk.audit.AuditClient;
import com.tencent.bk.audit.constants.AccessTypeEnum;
import com.tencent.bk.audit.constants.UserIdentifyTypeEnum;
import com.tencent.bk.audit.context.ActionAuditContext;
import com.tencent.bk.audit.context.AuditContext;

import java.util.Arrays;

import static com.tencent.bk.audit.constants.AuditAttributeNames.INSTANCE_ID;
import static com.tencent.bk.audit.constants.AuditAttributeNames.INSTANCE_NAME;

/**
 * 一次操作（请求）产生多个审计事件
 */
public class MultiAuditEventExample {
    private final AuditClient auditClient;

    public MultiAuditEventExample(AuditClient auditClient) {
        this.auditClient = auditClient;
    }

    public void run() {
        // 构造审计上下文
        AuditContext auditContext = auditClient.auditContextBuilder("edit_job_plan")
                .setSystemId("bk_job")
                .setRequestId("3a84858499bd71d674bc40d4f73cb41a")
                .setAccessSourceIp("127.0.0.1")
                .setAccessType(AccessTypeEnum.CONSOLE)
                .setAccessUserAgent("Chrome")
                .setUserIdentifyType(UserIdentifyTypeEnum.PERSONAL)
                .setUsername("admin")
                .build();
        // 对批量操作进行审计
        auditClient.audit(auditContext, this::batchAction);
    }

    private void batchAction() {
        // 使用 ActionAuditContext 封装 Action 代码，自动封装审计逻辑
        ActionAuditContext.builder("edit_job_plan")
                .setResourceType("job_plan")
                // 如果传入操作实例列表，那么每个操作操作都会产生一个审计事件
                .setInstanceIdList(Arrays.asList("1000", "1001"))
                .setInstanceNameList(Arrays.asList("plan1", "plan2"))
                .setContent("Edit job plan [{{" + INSTANCE_NAME + "}}]({{" + INSTANCE_ID + "}})")
                .setScopeType("biz")
                .setScopeId("2")
                .build()
                .wrapActionRunnable(() -> {
                    //  action code
                })
                .run();
    }


}
