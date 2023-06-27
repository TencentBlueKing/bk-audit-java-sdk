package com.tencent.bk.audit;

import com.tencent.bk.audit.exporter.EventExporter;
import com.tencent.bk.audit.model.AuditContext;
import com.tencent.bk.audit.model.AuditEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * 审计入口服务
 */
@Slf4j
public class Audit {

    private final ThreadLocalAuditContextHolder auditContextHolder = new ThreadLocalAuditContextHolder();
    private final EventExporter eventExporter;

    public Audit(EventExporter eventExporter) {
        this.eventExporter = eventExporter;
        GlobalAuditRegistry.register(this);
    }

    /**
     * 开始审计
     *
     * @param auditContext 审计上下文
     */
    public void startAudit(AuditContext auditContext) {
        if (auditContextHolder.current() != null) {
            log.error("Current audit context is already exist! ");
            return;
        }
        auditContextHolder.set(auditContext);
    }

    /**
     * 返回当前审计上下文
     *
     * @return 当前审计上下文
     */
    public AuditContext currentAuditContext() {
        AuditContext auditContext = auditContextHolder.current();
        return auditContext != null ? auditContext : AuditContext.INVALID;
    }

    /**
     * 是否正在记录审计事件
     */
    public boolean isRecording() {
        return currentAuditContext() != null;
    }

    /**
     * 结束审计，输出审计事件
     */
    public void stopAudit() {
        try {
            AuditContext auditContext = currentAuditContext();
            auditContext.end();
            List<AuditEvent> auditEvents = auditContext.getEvents();
            if (CollectionUtils.isNotEmpty(auditEvents)) {
                eventExporter.export(auditEvents);
            }
        } catch (Throwable e) {
            // 忽略审计错误，避免影响业务代码执行
            log.error("Audit stop caught exception", e);
        } finally {
            auditContextHolder.reset();
        }
    }
}
