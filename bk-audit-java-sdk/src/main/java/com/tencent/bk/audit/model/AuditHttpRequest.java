package com.tencent.bk.audit.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 操作 - HTTP请求
 */
@Data
@NoArgsConstructor
public class AuditHttpRequest {
    private String uri;
    private String queryParams;
    private Object body;

    public AuditHttpRequest(String uri, String queryParams, Object body) {
        this.uri = uri;
        this.queryParams = queryParams;
        this.body = body;
    }
}
