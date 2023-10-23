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

//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public String getAccount() {
//        return account;
//    }
//
//    public void setAccount(String account) {
//        this.account = account;
//    }
//
//    public String getCreator() {
//        return creator;
//    }
//
//    public void setCreator(String creator) {
//        this.creator = creator;
//    }
//
//    public String getOs() {
//        return os;
//    }
//
//    public void setOs(String os) {
//        this.os = os;
//    }
//
//    public String getAlias() {
//        return alias;
//    }
//
//    public void setAlias(String alias) {
//        this.alias = alias;
//    }
//
//    public String getCreateTime() {
//        return createTime;
//    }
//
//    public void setCreateTime(String createTime) {
//        this.createTime = createTime;
//    }
}
