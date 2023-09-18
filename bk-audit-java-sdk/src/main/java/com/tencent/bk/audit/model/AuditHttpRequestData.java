package com.tencent.bk.audit.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 审计操作 - HTTP请求
 */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuditHttpRequestData {
    /**
     * http 请求 URI
     */
    @JsonProperty("uri")
    private String uri;

    /**
     * http 请求 QueryParams
     */
    @JsonProperty("queryParams")
    private String queryParams;

    /**
     * http 请求体
     */
    @JsonProperty("body")
    private Object body;

    public AuditHttpRequestData(String uri, String queryParams, Object body) {
        this.uri = uri;
        this.queryParams = queryParams;
        this.body = body;
    }
}
