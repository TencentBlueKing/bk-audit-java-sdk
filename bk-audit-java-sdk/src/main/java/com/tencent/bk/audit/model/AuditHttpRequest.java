package com.tencent.bk.audit.model;

import javax.servlet.http.HttpServletRequest;
import java.util.StringJoiner;

/**
 * 操作 - HTTP请求
 */
public class AuditHttpRequest {
    private final HttpServletRequest httpServletRequest;
    private final String uri;
    private final String queryParams;
    private Object body;

    public AuditHttpRequest(HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
        this.uri = httpServletRequest.getRequestURI();
        this.queryParams = httpServletRequest.getQueryString();
    }

    public void setBody(Object body) {
        this.body = body;
    }

    public HttpServletRequest getHttpServletRequest() {
        return httpServletRequest;
    }

    public String getUri() {
        return uri;
    }

    public String getQueryParams() {
        return queryParams;
    }

    public Object getBody() {
        return body;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", AuditHttpRequest.class.getSimpleName() + "[", "]")
                .add("uri='" + uri + "'")
                .add("queryParams='" + queryParams + "'")
                .add("body=" + body)
                .toString();
    }
}
