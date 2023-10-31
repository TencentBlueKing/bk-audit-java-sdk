package com.tencent.bk.audit;

import com.tencent.bk.audit.constants.AccessTypeEnum;
import com.tencent.bk.audit.constants.UserIdentifyTypeEnum;
import com.tencent.bk.audit.exception.AuditException;
import com.tencent.bk.audit.model.AuditHttpRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

/**
 * 默认的 AuditRequestProvider 实现。可以通过继承 DefaultAuditRequestProvider 自定义 AuditRequestProvider
 */
@Slf4j
public class DefaultAuditRequestProvider implements AuditRequestProvider {
    public static final String HEADER_USERNAME = "X-Username";
    public static final String HEADER_USER_IDENTIFY_TENANT_ID = "X-User-Identify-Tenant-Id";
    public static final String HEADER_USER_IDENTIFY_TYPE = "X-User-Identify-Type";
    public static final String HEADER_ACCESS_TYPE = "X-Access-Type";
    public static final String HEADER_REQUEST_ID = "X-Request-Id";

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
        HttpServletRequest httpServletRequest = getHttpServletRequest();
        return httpServletRequest.getHeader(HEADER_USERNAME);
    }

    @Override
    public UserIdentifyTypeEnum getUserIdentifyType() {
        HttpServletRequest httpServletRequest = getHttpServletRequest();
        return UserIdentifyTypeEnum.valOf(httpServletRequest.getHeader(HEADER_USER_IDENTIFY_TYPE));
    }

    @Override
    public String getUserIdentifyTenantId() {
        HttpServletRequest httpServletRequest = getHttpServletRequest();
        return httpServletRequest.getHeader(HEADER_USER_IDENTIFY_TENANT_ID);
    }

    @Override
    public AccessTypeEnum getAccessType() {
        HttpServletRequest httpServletRequest = getHttpServletRequest();
        return AccessTypeEnum.valOf(httpServletRequest.getHeader(HEADER_ACCESS_TYPE));
    }

    @Override
    public String getRequestId() {
        try {
            HttpServletRequest httpServletRequest = getHttpServletRequest();
            return httpServletRequest.getHeader(HEADER_REQUEST_ID);
        } catch (Throwable e) {
            log.error("Get request id error", e);
            return UUID.randomUUID().toString().replace("-", "");
        }
    }

    @Override
    public String getClientIp() {
        HttpServletRequest request = getHttpServletRequest();
        String xff = request.getHeader("X-Forwarded-For");
        if (xff == null) {
            return request.getRemoteAddr();
        } else {
            return xff.contains(",") ? xff.split(",")[0] : xff;
        }
    }

    @Override
    public String getUserAgent() {
        HttpServletRequest request = getHttpServletRequest();
        return request.getHeader("User-Agent");
    }
}
