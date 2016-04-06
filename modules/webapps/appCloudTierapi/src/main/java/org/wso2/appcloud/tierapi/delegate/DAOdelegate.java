package org.wso2.appcloud.tierapi.delegate;

import org.wso2.appcloud.tierapi.bean.Subscription;
import org.wso2.appcloud.tierapi.dao.ContainerSpecsDao;
import org.wso2.appcloud.tierapi.dao.PlanDao;
import org.wso2.appcloud.tierapi.dao.SubscriptionDao;
import org.wso2.appcloud.tierapi.dao.impl.ContainerSpecDaoImpl;
import org.wso2.appcloud.tierapi.dao.impl.PlanDaoImpl;
import org.wso2.appcloud.tierapi.dao.impl.SubscriptionDaoImpl;

public class DAOdelegate {

    private static PlanDao planInstance = new PlanDaoImpl();
    private static ContainerSpecsDao containerSpecInstance = new ContainerSpecDaoImpl();
    private static SubscriptionDao subscriptionInstance = new SubscriptionDaoImpl();
        
    /**
     * Get PlanDAO object
     */
    public static PlanDao getPlanInstance() {
        return planInstance;
    }
    
    /**
     * Get PlanContainerSpec object
     */
    public static ContainerSpecsDao getContainerSpecInstance() {
        return containerSpecInstance;
    }

    /**
     * Get SubscriptionDao object
     * 
     */
    public static SubscriptionDao getSubscriptionInstance() {
        return subscriptionInstance;
    }

}
