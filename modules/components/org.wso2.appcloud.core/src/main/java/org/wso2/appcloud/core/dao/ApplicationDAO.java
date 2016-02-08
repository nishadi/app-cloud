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

package org.wso2.appcloud.core.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.appcloud.common.AppCloudException;
import org.wso2.appcloud.core.DBUtil;
import org.wso2.appcloud.core.SQLQueryConstants;
import org.wso2.appcloud.core.dto.Application;
import org.wso2.appcloud.core.dto.ApplicationRuntime;
import org.wso2.appcloud.core.dto.ApplicationSummery;
import org.wso2.appcloud.core.dto.ApplicationType;
import org.wso2.appcloud.core.dto.Endpoint;
import org.wso2.appcloud.core.dto.Label;
import org.wso2.appcloud.core.dto.RuntimeProperty;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * DAO class for persisting or retrieving application related data to database
 */
public class ApplicationDAO {

    private static final Log log = LogFactory.getLog(ApplicationDAO.class);

    /**
     * Method for adding application to database
     *
     * @param application  application object
     * @param dbConnection database connection, since this is a part of full transaction using a common connection
     * @return
     * @throws AppCloudException
     */
    public boolean addApplication(Application application, int tenantId, Connection dbConnection) throws AppCloudException {

        PreparedStatement preparedStatement = null;

        try {

            preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.ADD_APPLICATION);
            preparedStatement.setString(1, application.getApplicationName());
            preparedStatement.setString(2, application.getDescription());
            preparedStatement.setInt(3, tenantId);
            preparedStatement.setString(4, application.getRevision());
            preparedStatement.setInt(5, application.getRuntimeId());
            preparedStatement.setString(6, application.getApplicationType());
            preparedStatement.setString(7, application.getEndpointURL());
            preparedStatement.setString(8, application.getStatus());
            preparedStatement.setInt(9, application.getNumberOfReplicas());

            preparedStatement.execute();

        } catch (SQLException e) {

            String msg =
                    "Error occurred while adding application : " + application.getApplicationName() + " to database " +
                    "in tenant : " + tenantId;
            throw new AppCloudException(msg, e);

        } finally {
            DBUtil.closePreparedStatement(preparedStatement);
        }

        return true;
    }

    /**
     * Method for adding label, which associated with an application, to database
     *
     * @param label        label object
     * @param dbConnection database connection, since this is a part of full transaction using a common connection
     * @return
     * @throws AppCloudException
     */
    public boolean addLabel(Label label, int applicationId, int tenantId, Connection dbConnection) throws AppCloudException {

        PreparedStatement preparedStatement = null;

        try {

            preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.ADD_LABEL);
            preparedStatement.setString(1, label.getLabelName());
            preparedStatement.setString(2, label.getLabelValue());
            preparedStatement.setInt(3, applicationId);
            preparedStatement.setInt(4, tenantId);
            preparedStatement.setString(5, label.getDescription());

            preparedStatement.execute();

        } catch (SQLException e) {

            String msg =
                    "Error occurred while adding the label : " + label.getLabelName() + " to the database in tenant" +
                    " : " + tenantId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closePreparedStatement(preparedStatement);
        }

        return true;
    }


    /**
     * Method for adding runtime property, which belongs to an application, to the database
     *
     * @param runtimeProperty runtime property object
     * @param dbConnection    database connection, since this is a part of full transaction using a common connection
     * @return
     * @throws AppCloudException
     */
    public boolean addRunTimeProperty(RuntimeProperty runtimeProperty, int applicationId, int tenantId, Connection dbConnection)
            throws AppCloudException {

        PreparedStatement preparedStatement = null;

        try {

            preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.ADD_RUNTIME_PROPERTY);
            preparedStatement.setString(1, runtimeProperty.getPropertyName());
            preparedStatement.setString(2, runtimeProperty.getPropertyValue());
            preparedStatement.setInt(3, applicationId);
            preparedStatement.setInt(4, tenantId);
            preparedStatement.setString(5, runtimeProperty.getDescription());

            preparedStatement.execute();

        } catch (SQLException e) {

            String msg = "Error occurred while adding the property : " + runtimeProperty.getPropertyName() +
                         " to the database in tenant" +
                         " : " + tenantId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);

        } finally {
            DBUtil.closePreparedStatement(preparedStatement);
        }

        return true;

    }


    /**
     * Method for adding endpoint, which is associated with an application, to database
     *
     * @param endpoint Endpoint object
     * @return
     * @throws AppCloudException
     */
    public boolean addEndpoint(Endpoint endpoint, String applicationName, String revision, int tenantId)
            throws AppCloudException {

        int applicationId = getIdOfApplication(applicationName, revision, tenantId);

        PreparedStatement preparedStatement = null;
        Connection dbConnection = DBUtil.getDBConnection();

        try {

            preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.ADD_ENDPOINT_URL);
            preparedStatement.setString(1, endpoint.getUrlValue());
            preparedStatement.setInt(2, applicationId);
            preparedStatement.setInt(3, tenantId);
            preparedStatement.setString(4, endpoint.getDescription());

            preparedStatement.execute();
            dbConnection.commit();

        } catch (SQLException e) {

            if (dbConnection != null) {
                log.error("Error while adding Endpoint : " + endpoint.getUrlValue() + " to database for " +
                          "tenant : " + tenantId + ". Transaction is being rolled back");
                try {
                    dbConnection.rollback();
                } catch (SQLException e1) {
                    log.error("Error while rolling back the failed transaction", e1);
                }
            }

            String msg = "Error occurred while adding the endpoint : " + endpoint.getUrlValue() +
                         " to the database in tenant" +
                         " : " + tenantId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);

        } finally {
            DBUtil.closePreparedStatement(preparedStatement);
            DBUtil.closeConnection(dbConnection);
        }

        return true;
    }


    /**
     * Method for getting the list of applications of a tenant from database with minimal information
     *
     * @param tenantId tenant id
     * @return
     * @throws AppCloudException
     */
    public List<ApplicationSummery> getAllApplicationsList(int tenantId) throws AppCloudException {

        Connection dbConnection = DBUtil.getDBConnection();
        PreparedStatement preparedStatement = null;

        List<ApplicationSummery> applicationSummeryList = new ArrayList<ApplicationSummery>();
        ApplicationSummery applicationSummery;

        try {

            preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.GET_ALL_APPLICATIONS_LIST);
            preparedStatement.setInt(1, tenantId);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                applicationSummery = new ApplicationSummery();
                applicationSummery.setApplicationId(resultSet.getInt(SQLQueryConstants.ID));
                applicationSummery.setApplicationName(resultSet.getString(SQLQueryConstants.APPLICATION_NAME));
                applicationSummery.setStatus(resultSet.getString(SQLQueryConstants.APPLICATION_STATUS));
                applicationSummery.setRuntimeName(resultSet.getString(SQLQueryConstants.RUNTIME_NAME));

                applicationSummeryList.add(applicationSummery);
            }

        } catch (SQLException e) {
            String msg = "Error while retrieving application list from database for tenant : " + tenantId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closePreparedStatement(preparedStatement);
            DBUtil.closeConnection(dbConnection);
        }
        return applicationSummeryList;
    }


    /**
     * Method for getting application from database using application id
     *
     * @param applicationName name of the application
     * @param revision        revision of the application
     * @return
     * @throws AppCloudException
     */
    public Application getApplicationByNameRevision(String applicationName, String revision, int tenantId)
            throws AppCloudException {

        Connection dbConnection = DBUtil.getDBConnection();
        PreparedStatement preparedStatement = null;

        Application application = new Application();

        try {

            preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.GET_APPLICATION_By_NAME_REVISION);
            preparedStatement.setString(1, applicationName);
            preparedStatement.setString(2, revision);
            preparedStatement.setInt(3, tenantId);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                application.setApplicationId(resultSet.getInt(SQLQueryConstants.ID));
                application.setApplicationName(resultSet.getString(SQLQueryConstants.APPLICATION_NAME));
                application.setDescription(resultSet.getString(SQLQueryConstants.DESCRIPTION));
                application.setStatus(resultSet.getString(SQLQueryConstants.APPLICATION_STATUS));
                application.setRuntimeId(resultSet.getInt(SQLQueryConstants.APPLICATION_RUNTIME_ID));
                application.setEndpointURL(resultSet.getString(SQLQueryConstants.APPLICATION_ENDPOINT_URL));
                application.setRevision(resultSet.getString(SQLQueryConstants.REVISION));
                application.setNumberOfReplicas(resultSet.getInt(SQLQueryConstants.NUMBER_OF_REPLICA));
                application.setApplicationType(resultSet.getString(SQLQueryConstants.APPLICATION_TYPE_NAME));
                application.setRuntimeName(resultSet.getString(SQLQueryConstants.RUNTIME_NAME));
            }

        } catch (SQLException e) {
            String msg =
                    "Error while retrieving application detail for application : " + applicationName + " revision :" +
                    " " + revision + " in tenant : " + tenantId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closePreparedStatement(preparedStatement);
            DBUtil.closeConnection(dbConnection);
        }
        return application;
    }


    /**
     * Method for getting the id of an application with the given revision
     *
     * @param applicationName application name
     * @param revision        application revision
     * @param tenantId        tenant id
     * @return
     * @throws AppCloudException
     */
    public int getIdOfApplication(String applicationName, String revision, int tenantId) throws AppCloudException {

        Connection dbConnection = DBUtil.getDBConnection();
        PreparedStatement preparedStatement = null;

        int applicationId = 0;

        try {

            preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.GET_APPLICATION_ID);
            preparedStatement.setString(1, applicationName);
            preparedStatement.setString(2, revision);
            preparedStatement.setInt(3, tenantId);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                applicationId = resultSet.getInt(SQLQueryConstants.ID);
            }

        } catch (SQLException e) {
            String msg =
                    "Error while retrieving the id of application : " + applicationName + " in tenant : " + tenantId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closePreparedStatement(preparedStatement);
            DBUtil.closeConnection(dbConnection);
        }

        return applicationId;
    }


    /**
     * Method for retrieving all the revisions of a given application
     *
     * @param applicationName name of the application
     * @param tenantId        tenant id
     * @return
     * @throws AppCloudException
     */
    public List<String> getAllRevisionsOfApplication(String applicationName, int tenantId) throws AppCloudException {

        Connection dbConnection = DBUtil.getDBConnection();
        PreparedStatement preparedStatement = null;

        List<String> revisionList = new ArrayList<String>();

        try {

            preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.GET_ALL_REVISIONS_OF_APPLICATION);
            preparedStatement.setString(1, applicationName);
            preparedStatement.setInt(2, tenantId);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                revisionList.add(resultSet.getString(SQLQueryConstants.REVISION));
            }

        } catch (SQLException e) {
            String msg = "Error while retrieving revisions from database for application : " + applicationName +
                         " in tenant : " + tenantId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closePreparedStatement(preparedStatement);
            DBUtil.closeConnection(dbConnection);
        }
        return revisionList;
    }


    /**
     * Method for retrieving all the endpoints of a given application
     *
     * @param applicationName id of the application
     * @param revision        revision of the application
     * @param tenantId        tenant id
     * @return
     * @throws AppCloudException
     */
    public List<Endpoint> getAllEndpointsOfApplication(String applicationName, String revision, int tenantId)
            throws AppCloudException {

        Connection dbConnection = DBUtil.getDBConnection();
        PreparedStatement preparedStatement = null;

        List<Endpoint> endpointList = new ArrayList<Endpoint>();
        Endpoint endpoint;

        try {

            preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.GET_ALL_ENDPOINT_URL_OF_APPLICATION);
            preparedStatement.setString(1, applicationName);
            preparedStatement.setString(2, revision);
            preparedStatement.setInt(3, tenantId);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                endpoint = new Endpoint();
                endpoint.setId(resultSet.getInt(SQLQueryConstants.ID));
                endpoint.setDescription(resultSet.getString(SQLQueryConstants.DESCRIPTION));
                endpoint.setUrlValue(resultSet.getString(SQLQueryConstants.ENDPOINT_URL_VALUE));

                endpointList.add(endpoint);
            }

        } catch (SQLException e) {
            String msg = "Error while retrieving Application endpoints from database for application : " +
                         applicationName + " revision : " + revision + " in tenant : " + tenantId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closePreparedStatement(preparedStatement);
            DBUtil.closeConnection(dbConnection);
        }
        return endpointList;
    }


    /**
     * Method for retrieving list of labels belongs to a given application
     *
     * @param applicationName name of the application
     * @param revision        revision of the application
     * @param tenantId        tenant id
     * @return
     * @throws AppCloudException
     */
    public List<Label> getAllLabelsOfApplication(String applicationName, String revision, int tenantId)
            throws AppCloudException {

        Connection dbConnection = DBUtil.getDBConnection();
        PreparedStatement preparedStatement = null;

        List<Label> labelList = new ArrayList<Label>();
        Label label;

        try {

            preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.GET_ALL_LABELS_OF_APPLICATION);
            preparedStatement.setString(1, applicationName);
            preparedStatement.setString(2, revision);
            preparedStatement.setInt(3, tenantId);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                label = new Label();
                label.setLabelId(resultSet.getInt(SQLQueryConstants.ID));
                label.setLabelName(resultSet.getString(SQLQueryConstants.LABEL_NAME));
                label.setLabelValue(resultSet.getString(SQLQueryConstants.LABEL_VALUE));
                label.setDescription(resultSet.getString(SQLQueryConstants.DESCRIPTION));

                labelList.add(label);
            }

        } catch (SQLException e) {
            String msg =
                    "Error while retrieving labels from database for application : " + applicationName + " revision :" +
                    " " + revision + " in tenant : " + tenantId;
            ;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closePreparedStatement(preparedStatement);
            DBUtil.closeConnection(dbConnection);
        }
        return labelList;
    }


    /**
     * Method for retrieving all the runtime properties of a given application
     *
     * @param applicationName name of the application
     * @param revision        revision of the application
     * @param tenantId        tenant id
     * @return
     * @throws AppCloudException
     */
    public List<RuntimeProperty> getAllRuntimePropertiesOfApplication(String applicationName, String revision,
                                                                      int tenantId) throws AppCloudException {

        Connection dbConnection = DBUtil.getDBConnection();
        PreparedStatement preparedStatement = null;

        List<RuntimeProperty> runtimePropertyList = new ArrayList<RuntimeProperty>();
        RuntimeProperty runtimeProperty;

        try {

            preparedStatement = dbConnection.prepareStatement(
                    SQLQueryConstants.GET_ALL_RUNTIME_PROPERTIES_OF_APPLICATION);
            preparedStatement.setString(1, applicationName);
            preparedStatement.setString(2, revision);
            preparedStatement.setInt(3, tenantId);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                runtimeProperty = new RuntimeProperty();
                runtimeProperty.setId(resultSet.getInt(SQLQueryConstants.ID));
                runtimeProperty.setPropertyName(resultSet.getString(SQLQueryConstants.PROPERTY_NAME));
                runtimeProperty.setPropertyValue(resultSet.getString(SQLQueryConstants.PROPERTY_VALUE));
                runtimeProperty.setDescription(resultSet.getString(SQLQueryConstants.DESCRIPTION));

                runtimePropertyList.add(runtimeProperty);
            }

        } catch (SQLException e) {
            String msg = "Error while retrieving the runtime properties from the database for application : " +
                         applicationName + " revision : " + revision + " in tenant : " + tenantId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closePreparedStatement(preparedStatement);
            DBUtil.closeConnection(dbConnection);
        }
        return runtimePropertyList;
    }


    /**
     * Method for retrieving all the application types
     *
     * @return
     * @throws AppCloudException
     */
    public List<ApplicationType> getAllApplicationTypes() throws AppCloudException {

        Connection dbConnection = DBUtil.getDBConnection();
        PreparedStatement preparedStatement = null;

        List<ApplicationType> applicationTypeList = new ArrayList<ApplicationType>();
        ApplicationType applicationType;

        try {

            preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.GET_ALL_APP_TYPES);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                applicationType = new ApplicationType();
                applicationType.setId(resultSet.getInt(SQLQueryConstants.ID));
                applicationType.setAppTypeName(resultSet.getString(SQLQueryConstants.APPLICATION_TYPE_NAME));
                applicationType.setDescription(resultSet.getString(SQLQueryConstants.DESCRIPTION));

                applicationTypeList.add(applicationType);
            }

        } catch (SQLException e) {
            String msg = "Error while retrieving app types from database";
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closePreparedStatement(preparedStatement);
            DBUtil.closeConnection(dbConnection);
        }
        return applicationTypeList;
    }


    /**
     * Method for retrieving all the runtimes for a given application type
     *
     * @param appType application type
     * @return
     * @throws AppCloudException
     */
    public List<ApplicationRuntime> getRuntimesForAppType(String appType)
            throws AppCloudException {

        Connection dbConnection = DBUtil.getDBConnection();
        PreparedStatement preparedStatement = null;

        List<ApplicationRuntime> applicationRuntimeList = new ArrayList<ApplicationRuntime>();
        ApplicationRuntime applicationRuntime;

        try {

            preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.GET_RUNTIMES_FOR_APP_TYPE_OF_TENANT);
            preparedStatement.setString(1, appType);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                applicationRuntime = new ApplicationRuntime();
                applicationRuntime.setId(resultSet.getInt(SQLQueryConstants.ID));
                applicationRuntime.setRuntimeName(resultSet.getString(SQLQueryConstants.RUNTIME_NAME));
                applicationRuntime.setImageName(resultSet.getString(SQLQueryConstants.RUNTIME_IMAGE_NAME));
                applicationRuntime.setRepoURL(resultSet.getString(SQLQueryConstants.RUNTIME_REPO_URL));
                applicationRuntime.setTag(resultSet.getString(SQLQueryConstants.RUNTIME_TAG));

                applicationRuntimeList.add(applicationRuntime);
            }

        } catch (SQLException e) {
            String msg = "Error while retrieving list of runtime from database for app type : " + appType;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closePreparedStatement(preparedStatement);
            DBUtil.closeConnection(dbConnection);
        }
        return applicationRuntimeList;
    }


    /**
     * Method for updating the status of the application
     *
     * @param status          status of the application
     * @param applicationName name of the application
     * @param revision        revison of the application
     * @param tenantId        tenant id
     * @return
     * @throws AppCloudException
     */
    public boolean updateApplicationStatus(String status, String applicationName, String revision, int tenantId)
            throws AppCloudException {

        Connection dbConnection = DBUtil.getDBConnection();
        PreparedStatement preparedStatement = null;

        try {

            preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.UPDATE_APPLICATION_STATUS);
            preparedStatement.setString(1, status);
            preparedStatement.setString(2, applicationName);
            preparedStatement.setString(3, revision);
            preparedStatement.setInt(4, tenantId);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            String msg = "Error while updating application status : " + status + " for application : " + applicationName
                         + " revision : " + revision + " in tenant : " + tenantId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closePreparedStatement(preparedStatement);
            DBUtil.closeConnection(dbConnection);
        }
        return true;
    }


    /**
     * Method for updating number of replicas for an application
     *
     * @param numberOfReplicas number of replicas of application
     * @param applicationName  name of the application
     * @param revision         revision of the application
     * @param tenantId         tenant id
     * @return
     * @throws AppCloudException
     */
    public boolean updateNumberOfReplicas(int numberOfReplicas, String applicationName, String revision, int tenantId)
            throws AppCloudException {

        Connection dbConnection = DBUtil.getDBConnection();
        PreparedStatement preparedStatement = null;

        try {

            preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.UPDATE_NUMBER_OF_REPLICA);
            preparedStatement.setInt(1, numberOfReplicas);
            preparedStatement.setString(2, applicationName);
            preparedStatement.setString(3, revision);
            preparedStatement.setInt(4, tenantId);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            String msg = "Error while updating number of replicas for application : " + applicationName + " revision : "
                         + revision + " in tenant : " + tenantId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closePreparedStatement(preparedStatement);
            DBUtil.closeConnection(dbConnection);
        }
        return true;
    }

}
