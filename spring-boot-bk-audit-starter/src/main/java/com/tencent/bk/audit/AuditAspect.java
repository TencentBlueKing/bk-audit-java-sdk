package com.tencent.bk.audit;

import com.tencent.bk.audit.annotations.AuditEntry;
import com.tencent.bk.audit.annotations.AuditRequestBody;
import com.tencent.bk.audit.config.AuditProperties;
import com.tencent.bk.audit.context.AuditContext;
import com.tencent.bk.audit.model.AuditHttpRequest;
import com.tencent.bk.audit.model.ErrorInfo;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 审计事件入口切入点。
 * <p>
 * 使用@Order(Ordered.LOWEST_PRECEDENCE - 1) 保证 AuditAspect 比 ActionAuditAspect 先执行
 */
@Aspect
@Slf4j
@Order(Ordered.LOWEST_PRECEDENCE - 1)
public class AuditAspect {
    private final AuditClient auditClient;
    private final AuditRequestProvider auditRequestProvider;
    private final AuditExceptionResolver auditExceptionResolver;
    private final AuditProperties auditProperties;

    public AuditAspect(AuditClient auditClient,
                       AuditRequestProvider auditRequestProvider,
                       AuditExceptionResolver auditExceptionResolver,
                       AuditProperties auditProperties) {
        this.auditClient = auditClient;
        this.auditRequestProvider = auditRequestProvider;
        this.auditExceptionResolver = auditExceptionResolver;
        this.auditProperties = auditProperties;
        log.info("Init AuditAspect success");
    }


    // 声明审计事件入口切入点
    @Pointcut("@annotation(com.tencent.bk.audit.annotations.AuditEntry)")
    public void auditEntry() {
    }

    @Before("auditEntry()")
    public void startAudit(JoinPoint jp) {
        if (log.isDebugEnabled()) {
            log.debug("Start audit, entry: {}", jp.getSignature().toShortString());
        }

        long start = System.currentTimeMillis();
        try {
            Method method = ((MethodSignature) jp.getSignature()).getMethod();
            AuditEntry record = method.getAnnotation(AuditEntry.class);
            startAudit(jp, method, record);
        } catch (Throwable e) {
            // 忽略审计错误，避免影响业务代码执行
            log.error("Start audit context caught exception", e);
        } finally {
            if (log.isDebugEnabled()) {
                log.debug("Audit start, cost: {}", System.currentTimeMillis() - start);
            }
        }
    }

    private void startAudit(JoinPoint jp, Method method, AuditEntry record) {
        AuditContext auditContext = auditClient.auditContextBuilder(record.actionId())
                .setSubActionIds(record.subActionIds().length == 0 ? null : Arrays.asList(record.subActionIds()))
                .setUsername(auditRequestProvider.getUsername())
                .setAccessType(auditRequestProvider.getAccessType())
                .setAccessSourceIp(auditRequestProvider.getClientIp())
                .setUserIdentifyType(auditRequestProvider.getUserIdentifyType())
                .setUserIdentifyTenantId(auditRequestProvider.getUserIdentifyTenantId())
                .setBkAppCode(auditProperties.getBkAppCode())
                .setSystemId(auditProperties.getSystemId())
                .setRequestId(auditRequestProvider.getRequestId())
                .setAccessUserAgent(auditRequestProvider.getUserAgent())
                .setHttpRequest(parseRequest(jp, method, auditRequestProvider.getRequest()))
                .build();
        auditClient.startAudit(auditContext);
    }

    private AuditHttpRequest parseRequest(JoinPoint jp, Method method, AuditHttpRequest request) {
        Object[] args = jp.getArgs();
        Annotation[][] annotations = method.getParameterAnnotations();
        for (int i = 0; i < annotations.length; i++) {
            Object arg = args[i];
            boolean found = false;
            Annotation[] argAnnotations = annotations[i];
            if (argAnnotations == null || argAnnotations.length == 0) {
                continue;
            }
            for (Annotation annotation : argAnnotations) {
                if (annotation.annotationType().equals(AuditRequestBody.class)) {
                    found = true;
                    break;
                }
            }
            if (found) {
                request.setBody(arg);
                break;
            }
        }
        return request;
    }

    @After(value = "auditEntry()")
    public void stopAudit(JoinPoint jp) {
        long start = System.currentTimeMillis();
        try {
            auditClient.stopAudit();
        } catch (Throwable e) {
            // 忽略审计错误，避免影响业务代码执行
            log.error("Stop audit context caught exception", e);
        } finally {
            if (log.isDebugEnabled()) {
                log.debug("Stop audit, entry: {}, cost: {}", jp.getSignature().toShortString(),
                        System.currentTimeMillis() - start);
            }
        }
    }

    @AfterThrowing(value = "auditEntry()", throwing = "throwable")
    public void auditException(JoinPoint jp, Throwable throwable) {
        long start = System.currentTimeMillis();

        try {
            recordException(throwable);
        } finally {
            if (log.isDebugEnabled()) {
                log.debug("Audit exception, entry: {}, cost: {}", jp.getSignature().toShortString(),
                        System.currentTimeMillis() - start);
            }
        }
    }

    private void recordException(Throwable e) {
        ErrorInfo errorInfo = auditExceptionResolver.resolveException(e);
        auditClient.currentAuditContext().error(errorInfo.getErrorCode(), errorInfo.getErrorMessage());
    }
}
