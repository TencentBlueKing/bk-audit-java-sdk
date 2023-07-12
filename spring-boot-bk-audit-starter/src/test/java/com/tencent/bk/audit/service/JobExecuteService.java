package com.tencent.bk.audit.service;

import com.tencent.bk.audit.DefaultAuditEventBuilder;
import com.tencent.bk.audit.annotations.ActionAuditRecord;
import com.tencent.bk.audit.context.ActionAuditContext;
import com.tencent.bk.audit.model.AuditEvent;
import com.tencent.bk.audit.model.Host;
import com.tencent.bk.audit.utils.AuditInstanceUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 作业执行 Service
 */
@Service
public class JobExecuteService {

    /**
     * 快速执行脚本
     *
     * @param scriptId 脚本 ID
     * @param hosts    目标主机 IP
     */
    @ActionAuditRecord(
            actionId = "execute_script",
            builder = ExecuteJobAuditEventBuilder.class,
            content = "Execute script [{{@SCRIPT_NAME}}]({{@SCRIPT_ID}})"
    )
    public void executeScript(long scriptId, List<Host> hosts) {
        ActionAuditContext.current()
                .setInstanceIdList(hosts.stream()
                        .map(host -> String.valueOf(host.getHostId())).collect(Collectors.toList()))
                .setInstanceNameList(hosts.stream().map(Host::getIp).collect(Collectors.toList()))
                .addAttribute("@SCRIPT_NAME", "script_" + scriptId)
                .addAttribute("@SCRIPT_ID", String.valueOf(scriptId));
        // action code here
    }

    /**
     * 自定义的作业执行审计事件生成
     */
    public static class ExecuteJobAuditEventBuilder extends DefaultAuditEventBuilder {
        private final ActionAuditContext actionAuditContext;

        public ExecuteJobAuditEventBuilder() {
            this.actionAuditContext = ActionAuditContext.current();
        }

        @Override
        public List<AuditEvent> build() {
            AuditEvent auditEvent = buildBasicAuditEvent();

            // 由于执行脚本的资源实例是主机，而不是脚本。一次操作可能有上万个主机，为每个主机实例生成一个审计事件会产生大量的审计事件。
            // 所以，这里把主机 ID 用逗号拼接，上报给审计中心，审计中心检索资源实例的时候会特殊处理。
            auditEvent.setResourceTypeId("host");
            // 执行脚本虽然包含多个主机资源实例，但是要求多个资源实例都放到一个实例中（id1,id2,id2..idN）
            auditEvent.setInstanceId(AuditInstanceUtils.extractInstanceIds(actionAuditContext.getInstanceIdList(),
                    instance -> instance));
            auditEvent.setInstanceName(AuditInstanceUtils.extractInstanceIds(actionAuditContext.getInstanceNameList(),
                    instance -> instance));

            // 事件描述
            auditEvent.setContent(resolveAttributes(actionAuditContext.getContent(), actionAuditContext.getAttributes()));

            return Collections.singletonList(auditEvent);
        }
    }
}
