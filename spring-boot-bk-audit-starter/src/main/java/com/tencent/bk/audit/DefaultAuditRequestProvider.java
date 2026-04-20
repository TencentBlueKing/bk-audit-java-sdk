package com.tencent.bk.audit;

import com.tencent.bk.audit.constants.AccessTypeEnum;
import com.tencent.bk.audit.constants.UserIdentifyTypeEnum;
import com.tencent.bk.audit.exception.AuditException;
import com.tencent.bk.audit.model.AuditHttpRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 默认的 AuditRequestProvider 实现，无真实有效审计字段。
 * 请通过继承DefaultAuditRequestProvider或直接实现AuditRequestProvider接口来提供相应的审计字段，
 * 从请求中读取数据作为审计字段时，需要注意数据是否真实可信，防止客户端伪造。
 */
@Slf4j
public class DefaultAuditRequestProvider implements AuditRequestProvider {

    @Override
    public AuditHttpRequest getRequest() {
        HttpServletRequest httpServletRequest = getHttpServletRequest();
        return new AuditHttpRequest(httpServletRequest);
    }

    private HttpServletRequest getHttpServletRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            log.error("Could not get RequestAttributes from RequestContext!");
            throw new AuditException("Parse http request error");
        }
        return ((ServletRequestAttributes) requestAttributes).getRequest();
    }

    @Override
    public String getUsername() {
        return "";
    }

    @Override
    public UserIdentifyTypeEnum getUserIdentifyType() {
        return UserIdentifyTypeEnum.UNKNOWN;
    }

    @Override
    public String getUserIdentifyTenantId() {
        return "";
    }

    @Override
    public AccessTypeEnum getAccessType() {
        return AccessTypeEnum.OTHER;
    }

    @Override
    public String getRequestId() {
        return "";
    }

    @Override
    public String getClientIp() {
        return "";
    }

    @Override
    public String getUserAgent() {
        return "";
    }
}
