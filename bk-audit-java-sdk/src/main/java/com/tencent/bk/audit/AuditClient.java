package com.tencent.bk.audit;

import com.tencent.bk.audit.context.AuditContext;
import com.tencent.bk.audit.context.AuditContextBuilder;
import com.tencent.bk.audit.context.LazyAuditContextHolder;
import com.tencent.bk.audit.exporter.EventExporter;
import com.tencent.bk.audit.model.AuditEvent;
import com.tencent.bk.audit.model.ErrorInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * 审计 SDK Client。
 * <p>AuditClient 是线程安全的。对于一个应用服务来说，只需要初始化一个单例的 AuditClient </p>
 */
@Slf4j
public class AuditClient {

    private final EventExporter eventExporter;
    private final AuditExceptionResolver auditExceptionResolver;

    public AuditClient(EventExporter eventExporter,
                       AuditExceptionResolver auditExceptionResolver) {
        this.eventExporter = eventExporter;
        this.auditExceptionResolver = auditExceptionResolver;
        GlobalAuditClient.register(this);
    }

    /**
     * 生成 AuditContextBuilder
     *
     * @param actionId 操作ID
     * @return AuditContextBuilder
     */
    public AuditContextBuilder auditContextBuilder(String actionId) {
        return AuditContextBuilder.builder(actionId);
    }


    /**
     * 开始操作审计
     *
     * @param auditContext 审计上下文
     */
    public AuditContext startAudit(AuditContext auditContext) {
        makeAuditContextCurrent(auditContext);
        return auditContext;
    }

    /**
     * 结束当前操作审计，输出审计事件
     */
    public void stopAudit() {
        try {
            AuditContext currentAuditContext = currentAuditContext();
            currentAuditContext.end();

            // 输出审计事件
            List<AuditEvent> auditEvents = currentAuditContext.getEvents();
            if (CollectionUtils.isEmpty(auditEvents)) {
                return;
            }
            eventExporter.export(auditEvents);
        } finally {
            LazyAuditContextHolder.get().reset();
        }
    }

    /**
     * 对操作进行审计。
     * <p>
     * 相比于 startAudit()/stopAudit() 这种方式，该方法自动封装了操作审计的所有细节，使用者无需关心审计的开启、记录、异常处理、结束以及输出审计事件。
     * <p>
     * 使用说明：<pre>{@code
     * AuditContext auditContext = auditClient.auditContextBuilder("execute_job").builder();
     * auditClient.audit(auditContext, () -> {
     *     // 操作代码
     * });
     * }
     * </pre>
     *
     * @param auditContext  审计上下文
     * @param auditRunnable 审计操作
     */
    public void audit(AuditContext auditContext, AuditRunnable auditRunnable) {
        audit(auditContext, () -> {
            auditRunnable.run();
            return null;
        });
    }

    /**
     * 对操作进行审计。
     * <p>
     * 相比于 startAudit()/stopAudit() 这种方式，该方法自动封装了操作审计的所有细节，使用者无需关心审计的开启、记录、异常处理、结束以及输出审计事件。
     * <p>
     * 使用说明：<pre>{@code
     * AuditContext auditContext = auditClient.auditContextBuilder("execute_job").builder();
     * auditClient.audit(auditContext, () -> {
     *     // 操作代码
     * });
     * }
     * </pre>
     *
     * @param auditContext  审计上下文
     * @param auditCallable 审计操作
     */
    public <V> V audit(AuditContext auditContext, AuditCallable<V> auditCallable) {
        if (log.isDebugEnabled()) {
            log.debug("Audit start");
        }
        long start = System.currentTimeMillis();

        // 设置为当前AuditContext，开始审计
        makeAuditContextCurrent(auditContext);

        // 执行 Action
        try {
            return auditCallable.call();
        } catch (Throwable throwable) {
            // 处理操作抛出的异常
            if (log.isDebugEnabled()) {
                log.debug("Audit action caught exception", throwable);
            }
            recordException(throwable);
            // 记录异常之后，需要把原始的异常抛出给上层继续处理
            throw throwable;
        } finally {
            // 结束审计
            stopAudit();
            if (log.isDebugEnabled()) {
                log.debug("Audit end, cost: {}", System.currentTimeMillis() - start);
            }
        }
    }

    private void makeAuditContextCurrent(AuditContext auditContext) {
        if (LazyAuditContextHolder.get().current() != null) {
            return;
        }
        LazyAuditContextHolder.get().set(auditContext);
    }

    /**
     * 解析、记录异常到当前审计上下文中
     *
     * @param throwable 异常
     */
    private void recordException(Throwable throwable) {
        ErrorInfo errorInfo = auditExceptionResolver.resolveException(throwable);
        currentAuditContext().error(errorInfo.getErrorCode(), errorInfo.getErrorMessage());
    }

    /**
     * 返回当前审计上下文
     *
     * @return 当前审计上下文
     */
    public AuditContext currentAuditContext() {
        return AuditContext.current();
    }

    /**
     * 是否正在记录审计事件
     */
    public boolean isRecording() {
        return currentAuditContext() != AuditContext.INVALID;
    }


}
