package org.wso2.appcloud.tierapi.server;

import java.sql.SQLException;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.wso2.appcloud.tierapi.bean.ContainerSpecifications;
import org.wso2.appcloud.tierapi.dao.impl.ContainerSpecDaoImpl;
import org.wso2.appcloud.tierapi.delegate.DAOdelegate;

@Path("/containerSpecs")
public class ContainerSpecSrvice {
    
    private ContainerSpecDaoImpl ContainerSpecInstance= (ContainerSpecDaoImpl) DAOdelegate.getContainerSpecInstance();
    
    /*
     * Get all Plans
     * @return                  Return all plans
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public List<ContainerSpecifications> getPlans() throws SQLException {
        
        return ContainerSpecInstance.getAllContainerSpecs();
    }

    /*
     * Get Plan using Plan ID
     * @param planId            Plan ID of the plan
     * @return                  Return the plan refer to the Plan ID
     */
    @GET
    @Path("/{containerSpecId}")
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public ContainerSpecifications getPlan(@PathParam("containerSpecId") int containerSpecId) throws SQLException {
        
        return ContainerSpecInstance.getContainerSpecById(containerSpecId);
    }

}
