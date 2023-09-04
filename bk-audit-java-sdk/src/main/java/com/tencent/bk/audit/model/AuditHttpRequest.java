package com.tencent.bk.audit.model;

import javax.servlet.http.HttpServletRequest;
import java.util.StringJoiner;

/**
 * 审计操作 - HTTP请求
 */
public class AuditHttpRequest {
    private final HttpServletRequest httpServletRequest;
    /**
     * http 请求 URI
     */
    private final String uri;

    /**
     * http 请求 QueryParams
     */
    private final String queryParams;

    /**
     * http 请求体
     */
    private Object body;

    public AuditHttpRequest(HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
        this.uri = httpServletRequest.getRequestURI();
        this.queryParams = httpServletRequest.getQueryString();
    }

    public void setBody(Object body) {
        this.body = body;
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

    public HttpServletRequest getHttpServletRequest() {
        return httpServletRequest;
    }

    public AuditHttpRequestData toAuditHttpRequestData() {
        return new AuditHttpRequestData(this.uri, this.queryParams, this.body);
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
