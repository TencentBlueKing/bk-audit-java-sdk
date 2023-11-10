package com.tencent.bk.audit.model;

/**
 * 作业执行方案
 */
public class JobPlan {
    /**
     * ID
     */
    private Long id;
    /**
     * 执行方案名称
     */
    private String name;
    /**
     * CMDB 业务 ID
     */
    private Long bizId;

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

    public Long getBizId() {
        return bizId;
    }

    public void setBizId(Long bizId) {
        this.bizId = bizId;
    }
}
