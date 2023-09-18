package com.tencent.bk.audit.example;

import com.tencent.bk.audit.AuditClient;
import com.tencent.bk.audit.constants.AccessTypeEnum;
import com.tencent.bk.audit.constants.UserIdentifyTypeEnum;
import com.tencent.bk.audit.context.ActionAuditContext;
import com.tencent.bk.audit.context.AuditContext;

import java.util.Arrays;
import java.util.Collections;

import static com.tencent.bk.audit.constants.AuditAttributeNames.INSTANCE_ID;
import static com.tencent.bk.audit.constants.AuditAttributeNames.INSTANCE_NAME;

/**
 * 一次操作（请求）包含多个 Action (比如 作业平台-删除作业模版，同时也会触发删除作业模版下的执行方案的操作)
 */
public class SubAuditEventExample {
    private final AuditClient auditClient;

    public SubAuditEventExample(AuditClient auditClient) {
        this.auditClient = auditClient;
    }

    public void run() {
        // 构造审计上下文
        AuditContext auditContext = auditClient.auditContextBuilder("delete_job_template")
                .setSystemId("bk_job")
                .setSubActionIds(Collections.singletonList("delete_job_plan"))
                .setRequestId("3a84858499bd71d674bc40d4f73cb41a")
                .setAccessSourceIp("127.0.0.1")
                .setAccessType(AccessTypeEnum.CONSOLE)
                .setAccessUserAgent("Chrome")
                .setUserIdentifyType(UserIdentifyTypeEnum.PERSONAL)
                .setUsername("admin")
                .build();
        // 对批量操作进行审计
        auditClient.audit(auditContext, this::deleteJobTemplateAndPlans);
    }

    private void deleteJobTemplateAndPlans() {
        deleteJobTemplate();
        batchDeleteJobPlan();
    }

    private void deleteJobTemplate() {
        // 使用 ActionAuditContext 封装 Action 代码，自动封装审计逻辑
        ActionAuditContext.builder("delete_job_template")
                .setResourceType("job_template")
                // 如果传入操作实例列表，那么每个操作操作都会产生一个审计事件
                .setInstanceId("1000")
                .setInstanceName("job_template_1")
                .setContent("Delete job template [{{" + INSTANCE_NAME + "}}]({{" + INSTANCE_ID + "}})")
                .build()
                .wrapActionRunnable(() -> {
                    //  action code
                })
                .run();
    }

    private void batchDeleteJobPlan() {
        // 使用 ActionAuditContext 封装 Action 代码，自动封装审计逻辑
        ActionAuditContext.builder("delete_job_plan")
                .setResourceType("job_plan")
                // 如果传入操作实例列表，那么每个操作操作都会产生一个审计事件
                .setInstanceIdList(Arrays.asList("1001", "1002"))
                .setInstanceNameList(Arrays.asList("plan1", "plan2"))
                .setContent("Delete job plan [{{" + INSTANCE_NAME + "}}]({{" + INSTANCE_ID + "}})")
                .build()
                .wrapActionRunnable(() -> {
                    //  action code
                })
                .run();
    }


}
