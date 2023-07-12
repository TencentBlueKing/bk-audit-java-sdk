package com.tencent.bk.audit.model;

import java.util.List;

public class ExecuteScriptRequest {
    private long scriptId;
    private List<Host> hosts;

    public long getScriptId() {
        return scriptId;
    }

    public void setScriptId(long scriptId) {
        this.scriptId = scriptId;
    }

    public List<Host> getHosts() {
        return hosts;
    }

    public void setHosts(List<Host> hosts) {
        this.hosts = hosts;
    }
}
