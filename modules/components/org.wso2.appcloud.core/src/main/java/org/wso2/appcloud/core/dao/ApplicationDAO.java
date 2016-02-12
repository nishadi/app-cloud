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

import java.io.InputStream;
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
     * @param application application object
     * @param tenantId    tenant id
     * @return true if application adding completed
     * @throws AppCloudException
     */
    public boolean addApplication(Application application, int tenantId) throws AppCloudException {
        PreparedStatement preparedStatement = null;
        Connection dbConnection = DBUtil.getDBConnection();

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
            dbConnection.commit();

        } catch (SQLException e) {

            String msg =
                    "Error occurred while adding application : " + application.getApplicationName() + " to database " +
                            "in tenant : " + tenantId;
            throw new AppCloudException(msg, e);

        } finally {
            DBUtil.closePreparedStatement(preparedStatement);
            DBUtil.closeConnection(dbConnection);
        }

        return true;
    }

    /**
     * Method for adding label, which associated with an application, to database
     *
     * @param applicationName     name of the application
     * @param applicationRevision revision of the application
     * @param tenantId            tenant id
     * @param labels              labels of the application
     * @return true if label adding completed
     * @throws AppCloudException
     */
    public boolean addLabel(String applicationName, String applicationRevision, int tenantId, List<Label> labels)
            throws AppCloudException {
        int applicationId = getIdOfApplication(applicationName, applicationRevision, tenantId);

        PreparedStatement preparedStatement = null;
        Connection dbConnection = DBUtil.getDBConnection();

        try {
            for (Label label : labels) {
                preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.ADD_LABEL);
                preparedStatement.setString(1, label.getLabelName());
                preparedStatement.setString(2, label.getLabelValue());
                preparedStatement.setInt(3, applicationId);
                preparedStatement.setInt(4, tenantId);
                preparedStatement.setString(5, label.getDescription());

                preparedStatement.execute();
            }
            dbConnection.commit();
        } catch (SQLException e) {

            String msg = "Error occurred while adding the label for application : " + applicationName
                    + " to the database in tenant" +
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
     * Method for adding runtime property, which belongs to an application, to the database
     *
     * @param applicationName     name of the application
     * @param applicationRevision revision of the application
     * @param tenantId            tenant id
     * @param runtimeProperties   runtime properties
     * @return true if runtime properties adding completed
     * @throws AppCloudException
     */
    public boolean addRunTimeProperty(String applicationName, String applicationRevision, int tenantId,
            List<RuntimeProperty> runtimeProperties) throws AppCloudException {
        int applicationId = getIdOfApplication(applicationName, applicationRevision, tenantId);

        PreparedStatement preparedStatement = null;
        Connection dbConnection = DBUtil.getDBConnection();

        try {
            for (RuntimeProperty runtimeProperty : runtimeProperties) {
                preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.ADD_RUNTIME_PROPERTY);
                preparedStatement.setString(1, runtimeProperty.getPropertyName());
                preparedStatement.setString(2, runtimeProperty.getPropertyValue());
                preparedStatement.setInt(3, applicationId);
                preparedStatement.setInt(4, tenantId);
                preparedStatement.setString(5, runtimeProperty.getDescription());

                preparedStatement.execute();
            }
            dbConnection.commit();
        } catch (SQLException e) {

            String msg = "Error occurred while adding the property for application : " + applicationName +
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
        ResultSet resultSet = null;
        try {

            preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.GET_ALL_APPLICATIONS_LIST);
            preparedStatement.setInt(1, tenantId);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                applicationSummery = new ApplicationSummery();
                applicationSummery.setApplicationId(resultSet.getInt(SQLQueryConstants.ID));
                applicationSummery.setApplicationName(resultSet.getString(SQLQueryConstants.APPLICATION_NAME));
                applicationSummery.setStatus(resultSet.getString(SQLQueryConstants.APPLICATION_STATUS));
                applicationSummery.setRuntimeName(resultSet.getString(SQLQueryConstants.RUNTIME_NAME));
                applicationSummery.setIcon(resultSet.getBlob(SQLQueryConstants.ICON));

                applicationSummeryList.add(applicationSummery);
            }

        } catch (SQLException e) {
            String msg = "Error while retrieving application list from database for tenant : " + tenantId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closeResultSet(resultSet);
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
                application.setIcon(resultSet.getBlob(SQLQueryConstants.ICON));
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
        ResultSet resultSet = null;
        int applicationId = 0;

        try {

            preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.GET_APPLICATION_ID);
            preparedStatement.setString(1, applicationName);
            preparedStatement.setString(2, revision);
            preparedStatement.setInt(3, tenantId);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                applicationId = resultSet.getInt(SQLQueryConstants.ID);
            }

        } catch (SQLException e) {
            String msg =
                    "Error while retrieving the id of application : " + applicationName + " in tenant : " + tenantId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closeResultSet(resultSet);
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
        ResultSet resultSet = null;
        List<String> revisionList = new ArrayList<String>();

        try {

            preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.GET_ALL_REVISIONS_OF_APPLICATION);
            preparedStatement.setString(1, applicationName);
            preparedStatement.setInt(2, tenantId);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                revisionList.add(resultSet.getString(SQLQueryConstants.REVISION));
            }

        } catch (SQLException e) {
            String msg = "Error while retrieving revisions from database for application : " + applicationName +
                         " in tenant : " + tenantId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closeResultSet(resultSet);
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
        ResultSet resultSet = null;
        try {

            preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.GET_ALL_ENDPOINT_URL_OF_APPLICATION);
            preparedStatement.setString(1, applicationName);
            preparedStatement.setString(2, revision);
            preparedStatement.setInt(3, tenantId);
            resultSet = preparedStatement.executeQuery();
            return generateEndpoints(resultSet);
        } catch (SQLException e) {
            String msg = "Error while retrieving Application endpoints from database for application : " +
                         applicationName + " revision : " + revision + " in tenant : " + tenantId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closeResultSet(resultSet);
            DBUtil.closePreparedStatement(preparedStatement);
            DBUtil.closeConnection(dbConnection);
        }
    }

    /**
     * Method for retrieving all the endpoints of a given application.
     *
     * @param applicationId application id
     * @return Endpoints list
     * @throws AppCloudException
     */
    public List<Endpoint> getAllEndpointsOfApplication(int applicationId) throws AppCloudException {

        Connection dbConnection = DBUtil.getDBConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = dbConnection.prepareStatement(
                    SQLQueryConstants.GET_ALL_ENDPOINT_URL_OF_APPLICATION_BY_ID);
            preparedStatement.setInt(1, applicationId);
            resultSet = preparedStatement.executeQuery();
            return generateEndpoints(resultSet);
        } catch (SQLException e) {
            String msg = "Error while retrieving Application endpoints from database for application :" + applicationId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closeResultSet(resultSet);
            DBUtil.closePreparedStatement(preparedStatement);
            DBUtil.closeConnection(dbConnection);
        }
    }

    private List<Endpoint> generateEndpoints(ResultSet resultSet) throws SQLException {
        List<Endpoint> endpointList = new ArrayList<Endpoint>();
        while (resultSet.next()) {
            Endpoint endpoint = new Endpoint();
            endpoint.setId(resultSet.getInt(SQLQueryConstants.ID));
            endpoint.setDescription(resultSet.getString(SQLQueryConstants.DESCRIPTION));
            endpoint.setUrlValue(resultSet.getString(SQLQueryConstants.ENDPOINT_URL_VALUE));
            endpointList.add(endpoint);
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
        ResultSet resultSet = null;
        try {

            preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.GET_ALL_LABELS_OF_APPLICATION);
            preparedStatement.setString(1, applicationName);
            preparedStatement.setString(2, revision);
            preparedStatement.setInt(3, tenantId);
            resultSet = preparedStatement.executeQuery();
            return generateLabels(resultSet);
        } catch (SQLException e) {
            String msg =
                    "Error while retrieving labels from database for application : " + applicationName + " revision :" +
                    " " + revision + " in tenant : " + tenantId;
            ;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closeResultSet(resultSet);
            DBUtil.closePreparedStatement(preparedStatement);
            DBUtil.closeConnection(dbConnection);
        }
    }

    /**
     * Method for retrieving list of labels belongs to a given application
     *
     * @param applicationId applicationId
     * @return Labels list
     * @throws AppCloudException
     */
    public List<Label> getAllLabelsOfApplication(int applicationId) throws AppCloudException {

        Connection dbConnection = DBUtil.getDBConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.GET_ALL_LABELS_OF_APPLICATION_BY_ID);
            preparedStatement.setInt(1, applicationId);
            resultSet = preparedStatement.executeQuery();
            return generateLabels(resultSet);
        } catch (SQLException e) {
            String msg = "Error while retrieving labels from database for application : " + applicationId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closeResultSet(resultSet);
            DBUtil.closePreparedStatement(preparedStatement);
            DBUtil.closeConnection(dbConnection);
        }
    }


    public void updateApplicationIcon(InputStream inputStream, String applicationName, int tenantId)
            throws AppCloudException {

        PreparedStatement preparedStatement = null;
        Connection dbConnection = DBUtil.getDBConnection();
        try {
            preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.UPDATE_APPLICATION_ICON);
            preparedStatement.setBlob(1, inputStream);
            preparedStatement.setString(2, applicationName);
            preparedStatement.setInt(3, tenantId);
            preparedStatement.execute();
            dbConnection.commit();

        } catch (SQLException e) {
            String msg = "Error occurred while adding application icon for application : " + applicationName
                         + " tenant id " + tenantId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);

        } finally {
            DBUtil.closePreparedStatement(preparedStatement);
            DBUtil.closeConnection(dbConnection);
        }
    }

    private List<Label> generateLabels(ResultSet resultSet) throws SQLException {
        List<Label> labelList = new ArrayList<Label>();
        while (resultSet.next()) {
            Label label = new Label();
            label.setLabelId(resultSet.getInt(SQLQueryConstants.ID));
            label.setLabelName(resultSet.getString(SQLQueryConstants.LABEL_NAME));
            label.setLabelValue(resultSet.getString(SQLQueryConstants.LABEL_VALUE));
            label.setDescription(resultSet.getString(SQLQueryConstants.DESCRIPTION));
            labelList.add(label);
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
        ResultSet resultSet = null;
        try {

            preparedStatement = dbConnection.prepareStatement(
                    SQLQueryConstants.GET_ALL_RUNTIME_PROPERTIES_OF_APPLICATION);
            preparedStatement.setString(1, applicationName);
            preparedStatement.setString(2, revision);
            preparedStatement.setInt(3, tenantId);
            resultSet = preparedStatement.executeQuery();
            return generateRuntimeProperties(resultSet);
        } catch (SQLException e) {
            String msg = "Error while retrieving the runtime properties from the database for application : " +
                         applicationName + " revision : " + revision + " in tenant : " + tenantId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closeResultSet(resultSet);
            DBUtil.closePreparedStatement(preparedStatement);
            DBUtil.closeConnection(dbConnection);
        }
    }

    /**
     * Method for retrieving all the runtime properties of a given application
     *
     * @param applicationId applicationId
     * @return RuntimeProperty List
     * @throws AppCloudException
     */
    public List<RuntimeProperty> getAllRuntimePropertiesOfApplication(int applicationId) throws AppCloudException {

        Connection dbConnection = DBUtil.getDBConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = dbConnection.prepareStatement(
                    SQLQueryConstants.GET_ALL_RUNTIME_PROPERTIES_OF_APPLICATION_BY_ID);
            preparedStatement.setInt(1, applicationId);
            resultSet = preparedStatement.executeQuery();
            return generateRuntimeProperties(resultSet);

        } catch (SQLException e) {
            String msg = "Error while retrieving the runtime properties from the database " +
                         "for application : "+applicationId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closeResultSet(resultSet);
            DBUtil.closePreparedStatement(preparedStatement);
            DBUtil.closeConnection(dbConnection);
        }
    }

    private List<RuntimeProperty> generateRuntimeProperties(ResultSet resultSet)
            throws SQLException {
        List<RuntimeProperty> runtimePropertyList = new ArrayList<RuntimeProperty>();
        while (resultSet.next()) {
            RuntimeProperty runtimeProperty = new RuntimeProperty();
            runtimeProperty.setId(resultSet.getInt(SQLQueryConstants.ID));
            runtimeProperty.setPropertyName(resultSet.getString(SQLQueryConstants.PROPERTY_NAME));
            runtimeProperty.setPropertyValue(resultSet.getString(SQLQueryConstants.PROPERTY_VALUE));
            runtimeProperty.setDescription(resultSet.getString(SQLQueryConstants.DESCRIPTION));
            runtimePropertyList.add(runtimeProperty);
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
        ResultSet resultSet = null;

        List<ApplicationType> applicationTypeList = new ArrayList<ApplicationType>();
        ApplicationType applicationType;

        try {

            preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.GET_ALL_APP_TYPES);
            resultSet = preparedStatement.executeQuery();

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
            DBUtil.closeResultSet(resultSet);
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
        ResultSet resultSet = null;
        List<ApplicationRuntime> applicationRuntimeList = new ArrayList<ApplicationRuntime>();
        ApplicationRuntime applicationRuntime;

        try {

            preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.GET_RUNTIMES_FOR_APP_TYPE_OF_TENANT);
            preparedStatement.setString(1, appType);
            resultSet = preparedStatement.executeQuery();

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
            DBUtil.closeResultSet(resultSet);
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
            dbConnection.commit();

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

    public void updateApplicationRuntimeProperty(int applicationId, String oldKey, String newKey, String oldValue, String newValue, int tenantId)
            throws AppCloudException {

        Connection dbConnection = DBUtil.getDBConnection();
        PreparedStatement preparedStatement = null;

        try {

            preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.UPDATE_RUNTIME_PROPERTIES);
            preparedStatement.setString(1, newKey);
            preparedStatement.setString(2, newValue);
            preparedStatement.setInt(3, applicationId);
            preparedStatement.setInt(4, tenantId);
            preparedStatement.setString(5, oldKey);
            preparedStatement.setString(6, oldValue);

            preparedStatement.executeUpdate();
            dbConnection.commit();
        } catch (SQLException e) {
            String msg = "Error while updating runtime property Key : " + oldKey + " for application : " + applicationId
                          + " in tenant : " + tenantId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closePreparedStatement(preparedStatement);
            DBUtil.closeConnection(dbConnection);
        }
    }
    public void updateTag(int applicationId, String oldKey, String newKey, String oldValue, String newValue, int tenantId)
            throws AppCloudException {
        Connection dbConnection = DBUtil.getDBConnection();
        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.UPDATE_TAG);
            preparedStatement.setString(1, newKey);
            preparedStatement.setString(2, newValue);
            preparedStatement.setInt(3, applicationId);
            preparedStatement.setInt(4, tenantId);
            preparedStatement.setString(5, oldKey);
            preparedStatement.setString(6, oldValue);

            preparedStatement.executeUpdate();
            dbConnection.commit();
        } catch (SQLException e) {
            String msg = "Error while updating tag Key : " + oldKey + " for application : " + applicationId
                          + " in tenant : " + tenantId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closePreparedStatement(preparedStatement);
            DBUtil.closeConnection(dbConnection);
        }
    }

    public boolean deleteApplicationRuntimeProperty(int applicationId, String key, String value, int tenantId)
            throws AppCloudException {

        Connection dbConnection = DBUtil.getDBConnection();
        PreparedStatement preparedStatement = null;
        boolean deleted=false;
        try {

            preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.DELETE_RUNTIME_PROPERTIES);
            preparedStatement.setInt(1, applicationId);
            preparedStatement.setInt(2, tenantId);
            preparedStatement.setString(3, key);
            preparedStatement.setString(4, value);

            deleted = preparedStatement.execute();
            dbConnection.commit();
        } catch (SQLException e) {
            String msg = "Error while deleting runtime property Key : " + key + " for application : " + applicationId
                          + " in tenant : " + tenantId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closePreparedStatement(preparedStatement);
            DBUtil.closeConnection(dbConnection);
        }
        return deleted;
    }
    public boolean deleteTag(int applicationId, String key, String value, int tenantId)
            throws AppCloudException {

        Connection dbConnection = DBUtil.getDBConnection();
        PreparedStatement preparedStatement = null;
        boolean deleted=false;
        try {

            preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.DELETE_TAG);
            preparedStatement.setInt(1, applicationId);
            preparedStatement.setInt(2, tenantId);
            preparedStatement.setString(3, key);
            preparedStatement.setString(4, value);

            deleted = preparedStatement.execute();
            dbConnection.commit();
        } catch (SQLException e) {
            String msg = "Error while deleting tag with Key : " + key + " for application : " + applicationId
                          + " in tenant : " + tenantId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closePreparedStatement(preparedStatement);
            DBUtil.closeConnection(dbConnection);
        }
        return deleted;
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

    public ApplicationRuntime getRuntime(int runtimeId) throws AppCloudException {

        Connection dbConnection = DBUtil.getDBConnection();
        PreparedStatement preparedStatement = null;
        ApplicationRuntime applicationRuntime = new ApplicationRuntime();
        ResultSet resultSet = null;

        try {

            preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.GET_RUNTIME_FOR_APP_TYPE);
            preparedStatement.setInt(1, runtimeId);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                applicationRuntime.setId(resultSet.getInt(SQLQueryConstants.ID));
                applicationRuntime.setImageName(resultSet.getString(SQLQueryConstants.RUNTIME_IMAGE_NAME));
                applicationRuntime.setRepoURL(resultSet.getString(SQLQueryConstants.RUNTIME_REPO_URL));
                applicationRuntime.setRuntimeName(resultSet.getString(SQLQueryConstants.RUNTIME_NAME));
                applicationRuntime.setTag(resultSet.getString(SQLQueryConstants.RUNTIME_TAG));
            }

        } catch (SQLException e) {
            String msg = "Error while retrieving runtime info from database for runtime : " + runtimeId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closeResultSet(resultSet);
            DBUtil.closePreparedStatement(preparedStatement);
            DBUtil.closeConnection(dbConnection);
        }
        return applicationRuntime;
    }

    /**
     * Delete a application
     *
     * @param applicationName
     * @param applicationRevision
     * @param tenantId
     * @return if delete a application
     * @throws AppCloudException
     */
    public boolean deleteApplication(String applicationName, String applicationRevision, int tenantId)
            throws AppCloudException {

        int applicationId = getIdOfApplication(applicationName, applicationRevision, tenantId);

        Connection dbConnection = DBUtil.getDBConnection();
        PreparedStatement preparedStatement = null;
        boolean deleted = false;
        try {

            preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.DELETE_APPLICATION);
            preparedStatement.setInt(1, applicationId);

            deleted = preparedStatement.execute();
            dbConnection.commit();
        } catch (SQLException e) {
            String msg =
                    "Error while deleting the application : " + applicationName + " revision : " + applicationRevision
                            + " in tenant : " + tenantId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closePreparedStatement(preparedStatement);
            DBUtil.closeConnection(dbConnection);
        }
        return deleted;
    }

    /**
     * Delete all aplication's revisions
     *
     * @param applicationName
     * @param applicationRevision
     * @param tenantId
     * @return if delete a application
     * @throws AppCloudException
     */
    public boolean deleteApplicationRevisions(String applicationName, int tenantId) throws AppCloudException {

        Connection dbConnection = DBUtil.getDBConnection();
        PreparedStatement preparedStatement = null;
        boolean deleted = false;
        try {

            preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.DELETE_APPLICATION_REVISION);
            preparedStatement.setString(1, applicationName);
            preparedStatement.setInt(2, tenantId);

            deleted = preparedStatement.execute();
            dbConnection.commit();
        } catch (SQLException e) {
            String msg = "Error while deleting the application : " + applicationName + " in tenant : " + tenantId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closePreparedStatement(preparedStatement);
            DBUtil.closeConnection(dbConnection);
        }
        return deleted;
    }

}
