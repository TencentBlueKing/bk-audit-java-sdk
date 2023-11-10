package com.tencent.bk.audit.model;

/**
 * 作业模版
 */
public class JobTemplate {
    /**
     * ID
     */
    private Long id;
    /**
     * CMDB 业务 ID
     */
    private Long bizId;
    /**
     * 名称
     */
    private String name;
    /**
     * 描述
     */
    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getBizId() {
        return bizId;
    }

    public void setBizId(Long bizId) {
        this.bizId = bizId;
    }
}
