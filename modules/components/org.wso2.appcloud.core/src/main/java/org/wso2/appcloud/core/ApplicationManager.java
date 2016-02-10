/*
* Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package org.wso2.appcloud.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.appcloud.common.AppCloudException;
import org.wso2.appcloud.core.dao.ApplicationDAO;
import org.wso2.appcloud.core.dto.*;
import org.wso2.carbon.context.CarbonContext;

import java.util.List;

/**
 * This class provide the interface for accessing the dao layer
 */
public class ApplicationManager {

    private static Log log = LogFactory.getLog(ApplicationManager.class);

    /**
     * Method for adding application
     *
     * @param application application object
     * @return
     * @throws AppCloudException
     */
    public static void addApplication(Application application) throws AppCloudException {
        ApplicationDAO applicationDAO = new ApplicationDAO();

        int tenantId = CarbonContext.getThreadLocalCarbonContext().getTenantId();
        boolean isApplicationAdded = applicationDAO.addApplication(application, tenantId);
        if (isApplicationAdded) {
            String applicationName = application.getApplicationName();
            String applicationRevision = application.getRevision();
            List<Label> labels = application.getLabels();
            addLabels(applicationName, applicationRevision, labels);
            List<RuntimeProperty> propertyList = application.getRuntimeProperties();
            addRuntimeProperties(applicationName, applicationRevision, propertyList);
        }
    }

    /**
     * Adding runtime properties for the application
     *
     * @param application
     * @param applicationId
     * @param tenantId
     * @throws AppCloudException
     */
    private static void addRuntimeProperties(String applicationName, String applicationRevision,
            List<RuntimeProperty> runtimeProperties) throws AppCloudException {
        ApplicationDAO applicationDAO = new ApplicationDAO();
        int tenantId = CarbonContext.getThreadLocalCarbonContext().getTenantId();
        if (runtimeProperties != null) {
            applicationDAO.addRunTimeProperty(applicationName, applicationRevision, tenantId, runtimeProperties);
        }
    }

    /**
     * Adding label for the application
     *
     * @param application
     * @param applicationId
     * @param tenantId
     * @throws AppCloudException
     */
    private static void addLabels(String applicationName, String applicationRevision, List<Label> labels)
            throws AppCloudException {
        ApplicationDAO applicationDAO = new ApplicationDAO();
        int tenantId = CarbonContext.getThreadLocalCarbonContext().getTenantId();
        if (labels != null) {
            applicationDAO.addLabel(applicationName, applicationRevision, tenantId, labels);
        }
    }

    /**
     * Method for adding endpoint associated with an application
     *
     * @param endpoint endpoint object
     * @return
     * @throws AppCloudException
     */
    public static boolean addEndpoint(Endpoint endpoint, String applicationName, String revision) throws AppCloudException {
        int tenantId = CarbonContext.getThreadLocalCarbonContext().getTenantId();
        ApplicationDAO applicationDAO = new ApplicationDAO();
        return applicationDAO.addEndpoint(endpoint, applicationName, revision, tenantId);
    }

    /**
     * Method for getting the list of application of a tenant
     *
     * @return
     * @throws AppCloudException
     */
    public static ApplicationSummery[] getApplicationList() throws AppCloudException {
        int tenantId = CarbonContext.getThreadLocalCarbonContext().getTenantId();
        ApplicationDAO applicationDAO = new ApplicationDAO();
        List<ApplicationSummery> applicationSummeries = applicationDAO.getAllApplicationsList(tenantId);
        return applicationSummeries.toArray(new ApplicationSummery[applicationSummeries.size()]);
    }

    /**
     * Method for getting all the revisions of a given application
     *
     * @param applicationName name of the application
     * @return
     * @throws AppCloudException
     */
    public static String[] getAllRevisionsOfApplication(String applicationName) throws AppCloudException {
        int tenantId = CarbonContext.getThreadLocalCarbonContext().getTenantId();
        ApplicationDAO applicationDAO = new ApplicationDAO();
        List<String> revisions = applicationDAO.getAllRevisionsOfApplication(applicationName, tenantId);
        return revisions.toArray(new String[revisions.size()]);
    }

    /**
     * Method for getting application by id
     *
     * @param applicationId if of the application
     * @return
     * @throws AppCloudException
     */

    /**
     *Method for getting application by id
     *
     * @param applicationName name of the application
     * @param revision revision of the application
     * @return
     * @throws AppCloudException
     */
    public static Application getApplicationByNameRevision(String applicationName, String revision) throws AppCloudException {
        ApplicationDAO applicationDAO = new ApplicationDAO();

        int tenantId = CarbonContext.getThreadLocalCarbonContext().getTenantId();
        try {
            Application application = applicationDAO.getApplicationByNameRevision(applicationName, revision, tenantId);
            int applicationId = application.getApplicationId();
            application.setEndpoints(applicationDAO.getAllEndpointsOfApplication(applicationId));
            application.setRuntimeProperties(applicationDAO.getAllRuntimePropertiesOfApplication(applicationId));
            application.setLabels(applicationDAO.getAllLabelsOfApplication(applicationId));
            return application;
        } catch (AppCloudException e) {
            String msg = "Error while getting the application detail for application : " + applicationName + " revision :" +
                         " " + revision + " in tenant : " + tenantId;
            throw new AppCloudException(msg, e);
        }
    }


    /**
     * Method for getting all apptypes
     *
     * @return
     * @throws AppCloudException
     */
    public static ApplicationType[] getAllAppTypes() throws AppCloudException {
        int tenantId = CarbonContext.getThreadLocalCarbonContext().getTenantId();
        ApplicationDAO applicationDAO = new ApplicationDAO();
        List<ApplicationType> applicationTypeList = applicationDAO.getAllApplicationTypes();
        return applicationTypeList.toArray(new ApplicationType[applicationTypeList.size()]);
    }


    /**
     * Method for getting all runtimes for a given application type
     *
     * @param appType application type
     * @return
     * @throws AppCloudException
     */
    public static ApplicationRuntime[] getAllRuntimesForAppType(String appType)
            throws AppCloudException {
        ApplicationDAO applicationDAO = new ApplicationDAO();
        List<ApplicationRuntime> runtimes = applicationDAO.getRuntimesForAppType(appType);
        return runtimes.toArray(new ApplicationRuntime[runtimes.size()]);
    }

    /**
     * Method for updating application status
     *
     * @param status status of application
     * @return
     * @throws AppCloudException
     */
    public static boolean updateApplicationStatus(String status, String applicationName, String revision) throws AppCloudException {
        int tenantId = CarbonContext.getThreadLocalCarbonContext().getTenantId();
        ApplicationDAO applicationDAO = new ApplicationDAO();
        return applicationDAO.updateApplicationStatus(status, applicationName, revision, tenantId);
    }


    /**
     * Method for updating the number of replicas for a given application
     *
     * @param numberOfReplica number of replicas, which should be persisted to database
     * @param applicationName name of the application
     * @param revision revision of the application
     * @return
     * @throws AppCloudException
     */
    public static boolean updateNumberOfReplicas(int numberOfReplica, String applicationName, String revision) throws AppCloudException {
        int tenantId = CarbonContext.getThreadLocalCarbonContext().getTenantId();
        ApplicationDAO applicationDAO = new ApplicationDAO();
        return applicationDAO.updateNumberOfReplicas(numberOfReplica, applicationName, revision, tenantId);
    }
}
