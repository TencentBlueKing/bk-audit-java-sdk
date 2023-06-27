package com.tencent.bk.audit;

import com.tencent.bk.audit.constants.AccessTypeEnum;
import com.tencent.bk.audit.constants.UserIdentifyTypeEnum;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

public class DefaultAuditRequestProvider implements AuditRequestProvider {
    public static final String HEADER_USERNAME = "X-Username";
    public static final String HEADER_USER_IDENTIFY_TENANT_ID = "X-User-Identify-Tenant-Id";
    public static final String HEADER_USER_IDENTIFY_TYPE = "X-User-Identify-Type";
    public static final String HEADER_ACCESS_TYPE = "X-Access-Type";
    public static final String HEADER_REQUEST_ID = "X-Request-Id";
    public static final String HEADER_BK_APP_CODE = "X-Bk-App-Code";

    @Override
    public HttpServletRequest getRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return null;
        }
        return ((ServletRequestAttributes) requestAttributes).getRequest();
    }

    @Override
    public String getUsername() {
        HttpServletRequest httpServletRequest = getRequest();
        return httpServletRequest.getHeader(HEADER_USERNAME);
    }

    @Override
    public UserIdentifyTypeEnum getUserIdentifyType() {
        HttpServletRequest httpServletRequest = getRequest();
        return UserIdentifyTypeEnum.valOf(httpServletRequest.getHeader(HEADER_USER_IDENTIFY_TYPE));
    }

    @Override
    public String getUserIdentifyTenantId() {
        HttpServletRequest httpServletRequest = getRequest();
        return httpServletRequest.getHeader(HEADER_USER_IDENTIFY_TENANT_ID);
    }

    @Override
    public AccessTypeEnum getAccessType() {
        HttpServletRequest httpServletRequest = getRequest();
        return AccessTypeEnum.valOf(httpServletRequest.getHeader(HEADER_ACCESS_TYPE));
    }

    @Override
    public String getRequestId() {
        HttpServletRequest httpServletRequest = getRequest();
        return httpServletRequest.getHeader(HEADER_REQUEST_ID);
    }

    @Override
    public String getBkAppCode() {
        HttpServletRequest httpServletRequest = getRequest();
        return httpServletRequest.getHeader(HEADER_BK_APP_CODE);
    }

    @Override
    public String getClientIp() {
        HttpServletRequest request = getRequest();
        String xff = request.getHeader("X-Forwarded-For");
        if (xff == null) {
            return request.getRemoteAddr();
        } else {
            return xff.contains(",") ? xff.split(",")[0] : xff;
        }
    }

    @Override
    public String getUserAgent() {
        HttpServletRequest request = getRequest();
        return request.getHeader("User-Agent");
    }
}
