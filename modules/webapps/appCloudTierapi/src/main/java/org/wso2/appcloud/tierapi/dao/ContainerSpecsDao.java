package org.wso2.appcloud.tierapi.dao;

import java.sql.SQLException;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.wso2.appcloud.tierapi.bean.ContainerSpecifications;

@XmlRootElement
public interface ContainerSpecsDao {
    
    /*
     * Get all defined Container Specs
     */
    public List<ContainerSpecifications> getAllContainerSpecs() throws SQLException;
    
    /*
     * Get Container Spec by ID
     */
    public ContainerSpecifications getContainerSpecById(int containerSpecId) throws SQLException;

    /*
     * Define new Container Spec
     */
    public ContainerSpecifications defineContainerSpec(ContainerSpecifications containerSpec) throws SQLException;

    /*
     * Delete Container Spec by ID
     */
    public boolean deleteContainerSpecById (int planId) throws SQLException;

    /*
     * Update Container Spec by ID
     */
    public ContainerSpecifications updateContainerSpecById(int containerSpecId, ContainerSpecifications containerSpec) throws SQLException;

}
