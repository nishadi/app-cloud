package org.wso2.appcloud.tierapi.dao;

import org.wso2.appcloud.tierapi.bean.ContainerSpecifications;
import org.wso2.appcloud.tierapi.bean.Plan;

import java.sql.SQLException;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public interface PlanDao {
    
    /*
     * Get all defined subscription plans
     */
    public List<Plan> getAllPlans() throws SQLException;
    
    /*
     * Get plan by ID
     */
    public Plan getPlanByPlanId(int planId) throws SQLException;

    /*
     * Define new Plan
     */
    public Plan definePlan(Plan plan) throws SQLException;

    /*
     * Delete Plan by ID
     */
    public boolean deletePlanById (int planId) throws SQLException;

    /*
     * Update Plan by ID
     */
    public Plan updatePlanById (int planId, Plan plan) throws SQLException;

    /*
     * Get the allowed container specifications within the subscription plan
     */
    public List<ContainerSpecifications> getAllowedConSpecs(int planId) throws SQLException ;


}