package com.tencent.bk.audit.model;

public class Host {
    private long hostId;
    private String ip;

    public Host() {
    }

    public Host(long hostId, String ip) {
        this.hostId = hostId;
        this.ip = ip;
    }

    public long getHostId() {
        return hostId;
    }

    public void setHostId(long hostId) {
        this.hostId = hostId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
