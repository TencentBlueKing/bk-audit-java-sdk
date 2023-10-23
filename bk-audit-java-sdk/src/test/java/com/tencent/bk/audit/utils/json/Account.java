package com.tencent.bk.audit.utils.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;

@Data
public class Account {
    @JsonPropertyDescription("Account ID")
    private Long id;

    @JsonPropertyDescription("Account")
    private String account;

    @JsonPropertyDescription("Account creator")
    private String creator;

    @JsonPropertyDescription("Account OS")
    private String os;

    @JsonPropertyDescription("Account alias")
    private String alias;

    @JsonPropertyDescription("Create time")
    @JsonProperty("create_time")
    private String createTime;
}
