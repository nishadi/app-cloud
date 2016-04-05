package org.wso2.appcloud.core.dto;


import java.sql.Timestamp;
import java.util.List;

public class Version {

    private String versionName;
    private String hashId;
    private int runtimeId;
    private String runtimeName;
    private String status;
    private List<RuntimeProperty> runtimeProperties;
    private List<Tag> tags;
    private int tenantId;
    private Timestamp createdTimestamp;

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getHashId() {
        return hashId;
    }

    public void setHashId(String hashId) {
        this.hashId = hashId;
    }

    public int getRuntimeId() {
        return runtimeId;
    }

    public void setRuntimeId(int runtimeId) {
        this.runtimeId = runtimeId;
    }

    public String getRuntimeName() {
        return runtimeName;
    }

    public void setRuntimeName(String runtimeName) {
        this.runtimeName = runtimeName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<RuntimeProperty> getRuntimeProperties() {
        return runtimeProperties;
    }

    public void setRuntimeProperties(List<RuntimeProperty> runtimeProperties) {
        this.runtimeProperties = runtimeProperties;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public int getTenantId() {
        return tenantId;
    }

    public void setTenantId(int tenantId) {
        this.tenantId = tenantId;
    }

    public Timestamp getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Timestamp createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }
}
