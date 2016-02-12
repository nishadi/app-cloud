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

import java.io.IOException;
import java.io.InputStream;
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
            addTags(applicationName, applicationRevision, labels);
            List<RuntimeProperty> propertyList = application.getRuntimeProperties();
            addRuntimeProperties(applicationName, applicationRevision, propertyList);
        }
    }

    /**
     *  Adding runtime properties for the application
     *
     * @param applicationName
     * @param applicationRevision
     * @param runtimeProperties
     * @throws AppCloudException
     */
    public static void addRuntimeProperties(String applicationName, String applicationRevision,
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
     * @param applicationName
     * @param applicationRevision
     * @param labels
     * @throws AppCloudException
     */
    public static void addTags(String applicationName, String applicationRevision, List<Label> labels)
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

    public static RuntimeProperty[] getApplicationRuntimePropertiesByNameRevision(String applicationName, String revision) throws AppCloudException {
        ApplicationDAO applicationDAO = new ApplicationDAO();

        int tenantId = CarbonContext.getThreadLocalCarbonContext().getTenantId();
        try {
            List<RuntimeProperty> runtimeProperties = applicationDAO.getAllRuntimePropertiesOfApplication(applicationName,revision,tenantId);
            return runtimeProperties.toArray(new RuntimeProperty[runtimeProperties.size()]);
        } catch (AppCloudException e) {
            String msg = "Error while getting the runtime properties for application : " + applicationName + " revision :" +
                         " " + revision + " in tenant : " + tenantId;
            throw new AppCloudException(msg, e);
        }
    }
    public static Label[] getTags(String applicationName, String revision) throws AppCloudException {
        ApplicationDAO applicationDAO = new ApplicationDAO();

        int tenantId = CarbonContext.getThreadLocalCarbonContext().getTenantId();
        try {
            List<Label> labels = applicationDAO.getAllLabelsOfApplication(applicationName, revision,
                    tenantId);
            return labels.toArray(new Label[labels.size()]);
        } catch (AppCloudException e) {
            String msg = "Error while getting the tags for application : " + applicationName + " revision :" +
                         " " + revision + " in tenant : " + tenantId;
            throw new AppCloudException(msg, e);
        }
    }


    public static void updateApplicationRuntimeProperty(String applicationName, String revision, String oldKey, String newKey, String oldValue, String newValue) throws AppCloudException {
        ApplicationDAO applicationDAO = new ApplicationDAO();

        int tenantId = CarbonContext.getThreadLocalCarbonContext().getTenantId();
        try {
            int applicationId = applicationDAO.getIdOfApplication(applicationName, revision, tenantId);
            applicationDAO.updateApplicationRuntimeProperty(applicationId, oldKey, newKey, oldValue, newValue,
                    tenantId);

        } catch (AppCloudException e) {
            String msg = "Error while getting the runtime properties for application : " + applicationName + " revision :" +
                         " " + revision + " in tenant : " + tenantId;
            throw new AppCloudException(msg, e);
        }
    }
    public static void updateTag(String applicationName, String revision, String oldKey, String newKey, String oldValue, String newValue) throws AppCloudException {
        ApplicationDAO applicationDAO = new ApplicationDAO();

        int tenantId = CarbonContext.getThreadLocalCarbonContext().getTenantId();
        try {
            int applicationId = applicationDAO.getIdOfApplication(applicationName, revision, tenantId);
            applicationDAO.updateTag(applicationId, oldKey, newKey, oldValue, newValue,
                    tenantId);

        } catch (AppCloudException e) {
            String msg = "Error while updating the tag for application : " + applicationName + " revision :" +
                         " " + revision + " in tenant : " + tenantId;
            throw new AppCloudException(msg, e);
        }
    }
    public static void deleteApplicationRuntimeProperty(String applicationName, String revision, String key, String value) throws AppCloudException {
        ApplicationDAO applicationDAO = new ApplicationDAO();

        int tenantId = CarbonContext.getThreadLocalCarbonContext().getTenantId();
        try {
            int applicationId = applicationDAO.getIdOfApplication(applicationName, revision, tenantId);
            applicationDAO.deleteApplicationRuntimeProperty(applicationId, key, value, tenantId);

        } catch (AppCloudException e) {
            String msg = "Error while getting the runtime properties for application : " + applicationName + " revision :" +
                         " " + revision + " in tenant : " + tenantId;
            throw new AppCloudException(msg, e);
        }
    }
    public static void deleteTag(String applicationName, String revision, String key, String value) throws AppCloudException {
        ApplicationDAO applicationDAO = new ApplicationDAO();

        int tenantId = CarbonContext.getThreadLocalCarbonContext().getTenantId();
        try {
            int applicationId = applicationDAO.getIdOfApplication(applicationName, revision, tenantId);
            applicationDAO.deleteTag(applicationId, key, value, tenantId);

        } catch (AppCloudException e) {
            String msg = "Error while getting the runtime properties for application : " + applicationName + " revision :" +
                         " " + revision + " in tenant : " + tenantId;
            throw new AppCloudException(msg, e);
        }
    }

    public static void updateApplicationIcon(String applicationName, Object iconStream)
            throws AppCloudException {
        ApplicationDAO applicationDAO = new ApplicationDAO();
        int tenantId = CarbonContext.getThreadLocalCarbonContext().getTenantId();
        if( iconStream instanceof InputStream){
            InputStream iconInputStream = (InputStream) iconStream;
            try {
                applicationDAO.updateApplicationIcon(iconInputStream, applicationName, tenantId);
            } catch (AppCloudException e) {
                String msg = "Error ouccered while updating application icon for application : " + applicationName +
                             " in tenant : " + tenantId;
                throw new AppCloudException(msg, e);
            } finally {
                try {
                    iconInputStream.close();
                } catch (IOException e) {
                    log.error("Error occurred while closing input stream for application : " + applicationName +
                              " in tenant : " + tenantId);
                }
            }
        } else {
            String msg = "Cannot read the provided icon stream for application : " + applicationName + " revision : " +
                         " in tenant : " + tenantId;
            log.error(msg);
            throw new AppCloudException(msg);
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
