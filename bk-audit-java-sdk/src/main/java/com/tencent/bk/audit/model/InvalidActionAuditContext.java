package com.tencent.bk.audit.model;

import java.util.List;
import java.util.Map;

public class InvalidActionAuditContext implements ActionAuditContext {
    @Override
    public ActionAuditScope makeCurrent() {
        return new NoopActionAuditScope();
    }

    @Override
    public ActionAuditContext addAttribute(String name, Object value) {
        return this;
    }


    @Override
    public void end() {

    }

    @Override
    public String getActionId() {
        return null;
    }

    @Override
    public Long getStartTime() {
        return null;
    }

    @Override
    public Long getEndTime() {
        return null;
    }

    @Override
    public String getResourceType() {
        return null;
    }

    @Override
    public List<String> getInstanceIdList() {
        return null;
    }

    @Override
    public List<String> getInstanceNameList() {
        return null;
    }

    @Override
    public List<Object> getOriginInstanceList() {
        return null;
    }

    @Override
    public List<Object> getInstanceList() {
        return null;
    }

    @Override
    public String getContent() {
        return null;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public List<AuditEvent> getEvents() {
        return null;
    }

    @Override
    public ActionAuditContext setInstanceIdList(List<String> instanceIdList) {
        return this;
    }

    @Override
    public ActionAuditContext setInstanceNameList(List<String> instanceNameList) {
        return this;
    }

    @Override
    public ActionAuditContext setOriginInstanceList(List<Object> originInstanceList) {
        return this;
    }

    @Override
    public ActionAuditContext setInstanceList(List<Object> instanceList) {
        return this;
    }

    @Override
    public ActionAuditContext setInstanceId(String instanceId) {
        return this;
    }

    @Override
    public ActionAuditContext setInstanceName(String instanceName) {
        return this;
    }

    @Override
    public ActionAuditContext setOriginInstance(Object originInstance) {
        return this;
    }

    @Override
    public ActionAuditContext setInstance(Object instance) {
        return this;
    }

    @Override
    public ActionAuditContext addInstanceInfo(String instanceId,
                                              String instanceName,
                                              Object originInstance,
                                              Object instance) {
        return this;
    }
}
