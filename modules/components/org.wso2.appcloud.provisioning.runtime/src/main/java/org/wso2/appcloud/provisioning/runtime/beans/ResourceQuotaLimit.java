package org.wso2.appcloud.provisioning.runtime.beans;

/**
 * Created by nadeeshani on 3/29/16.
 */
public class ResourceQuotaLimit {
    private String cpuLimit;
    private String memoryLimit;
    private String noOfPods;

    public void setCpuLimit(String cpuLimit){
        this.cpuLimit=cpuLimit;
    }
    public String getCpuLimit(){
        return cpuLimit;
    }
    public void setMemoryLimit(String memoryLimit){
        this.memoryLimit=memoryLimit;
    }
    public String getMemoryLimit(){
        return memoryLimit;
    }
    public void setNoOfPods(String noOfPods){
        this.noOfPods=noOfPods;
    }
    public String getNoOfPods(){
        return noOfPods;
    }
}
