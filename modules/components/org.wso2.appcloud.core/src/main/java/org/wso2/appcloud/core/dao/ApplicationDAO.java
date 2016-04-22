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

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.appcloud.common.AppCloudException;
import org.wso2.appcloud.core.DBUtil;
import org.wso2.appcloud.core.SQLQueryConstants;
import org.wso2.appcloud.core.dto.Application;
import org.wso2.appcloud.core.dto.ApplicationRuntime;
import org.wso2.appcloud.core.dto.ApplicationType;
import org.wso2.appcloud.core.dto.Container;
import org.wso2.appcloud.core.dto.ContainerServiceProxy;
import org.wso2.appcloud.core.dto.Deployment;
import org.wso2.appcloud.core.dto.RuntimeProperty;
import org.wso2.appcloud.core.dto.Tag;
import org.wso2.appcloud.core.dto.Transport;
import org.wso2.appcloud.core.dto.Version;
import org.wso2.carbon.user.core.tenant.JDBCTenantManager;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * DAO class for persisting or retrieving application related data to database
 */
public class ApplicationDAO {

    private static final Log log = LogFactory.getLog(ApplicationDAO.class);

    /**
     * Method for adding application details to database.
     *
     * @param dbConnection database connection
     * @param application  application object
     * @param tenantId     tenant id
     * @return
     * @throws AppCloudException
     */
    public void addApplication(Connection dbConnection, Application application, int tenantId) throws AppCloudException {

        PreparedStatement preparedStatement = null;
        int applicationId = 0;
        ResultSet resultSet = null;

        try {

            preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.ADD_APPLICATION,
                    Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, application.getApplicationName());
            preparedStatement.setString(2, application.getHashId());
            preparedStatement.setString(3, application.getDescription());
            preparedStatement.setInt(4, tenantId);
            preparedStatement.setString(5, application.getDefaultVersion());
            preparedStatement.setString(6, application.getApplicationType());

            preparedStatement.execute();

            resultSet = preparedStatement.getGeneratedKeys();
            while (resultSet.next()) {
                applicationId = resultSet.getInt(1);
            }

            List<Version> versions = application.getVersions();

            if (versions != null) {
                for (Version version : versions) {
                    addVersion(dbConnection, version, application.getApplicationName(), applicationId, tenantId);
                }
            }

            InputStream iconInputStream = null;
            if (application.getIcon() != null) {
                iconInputStream = IOUtils.toBufferedInputStream(application.getIcon().getBinaryStream());
            }
            updateApplicationIcon(dbConnection, iconInputStream, applicationId);

        } catch (SQLException e) {

            String msg =
                    "Error occurred while adding application : " + application.getApplicationName() + " to database " +
                            "in tenant : " + tenantId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);

        } catch (IOException e) {
            String msg =
                    "Error while generating stream of the icon for application : " + application.getApplicationName() +
                            " in tenant : " + tenantId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closeResultSet(resultSet);
            DBUtil.closePreparedStatement(preparedStatement);
        }

    }


    /**
     * Method for adding version details to database
     *
     * @param dbConnection  database connection
     * @param version       version object
     * @param applicationId application id
     * @param tenantId      tenant id
     * @return
     * @throws AppCloudException
     */
    public void addVersion(Connection dbConnection, Version version, String applicationName, int applicationId, int tenantId)
            throws AppCloudException {


        PreparedStatement preparedStatement = null;
        int versionId = 0;
        ResultSet resultSet = null;

        try {

            preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.ADD_VERSION, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, version.getVersionName());
            preparedStatement.setString(2, version.getHashId());
            preparedStatement.setInt(3, applicationId);
            preparedStatement.setInt(4, version.getRuntimeId());
            preparedStatement.setInt(5, tenantId);
            preparedStatement.setString(6, version.getConSpecCpu());
            preparedStatement.setString(7, version.getConSpecMemory());

            preparedStatement.execute();

            resultSet = preparedStatement.getGeneratedKeys();
            while (resultSet.next()) {
                versionId = resultSet.getInt(1);
            }

            List<Tag> tags = version.getTags();
            if (tags != null) {
                addTags(dbConnection, tags, version.getHashId(), tenantId);
            }

            List<RuntimeProperty> runtimeProperties = version.getRuntimeProperties();
            if (runtimeProperties != null) {
                addRunTimeProperties(dbConnection, runtimeProperties, version.getHashId(), tenantId);
            }

        } catch (SQLException e) {
            String msg = "Error occurred while adding application version to database for application id : " + applicationId +
                    " version : " + version.getVersionName() + " in tenant : " + tenantId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closeResultSet(resultSet);
            DBUtil.closePreparedStatement(preparedStatement);
        }

    }


    /**
     * Method for adding label, which associated with a version of an application, to database
     *
     * @param dbConnection  database Connection
     * @param tags          list of tags
     * @param versionHashId version hash id
     * @param tenantId      tenant id
     * @throws AppCloudException
     */
    public void addTags(Connection dbConnection, List<Tag> tags, String versionHashId, int tenantId)
            throws AppCloudException {

        PreparedStatement preparedStatement = null;

        try {

            preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.ADD_TAG);
            ;

            for (Tag tag : tags) {

                preparedStatement.setString(1, tag.getTagName());
                preparedStatement.setString(2, tag.getTagValue());
                preparedStatement.setString(3, versionHashId);
                preparedStatement.setString(4, tag.getDescription());
                preparedStatement.setInt(5, tenantId);

                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();

        } catch (SQLException e) {
            String msg = "Error occurred while adding tags to database for version with hash id : " + versionHashId + " in tenant : "
                    + tenantId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);

        } finally {
            DBUtil.closePreparedStatement(preparedStatement);
        }
    }

    /**
     * Method for adding runtime property, which belongs to a version of an application, to the database
     *
     * @param dbConnection      database connecton
     * @param runtimeProperties list of runtime properties
     * @param versionHashId     version hash id
     * @param tenantId          tenant id
     * @return
     * @throws AppCloudException
     */
    public boolean addRunTimeProperties(Connection dbConnection, List<RuntimeProperty> runtimeProperties, String versionHashId,
                                        int tenantId) throws AppCloudException {

        PreparedStatement preparedStatement = null;

        try {

            preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.ADD_RUNTIME_PROPERTY);

            for (RuntimeProperty runtimeProperty : runtimeProperties) {

                preparedStatement.setString(1, runtimeProperty.getPropertyName());
                preparedStatement.setString(2, runtimeProperty.getPropertyValue());
                preparedStatement.setString(3, versionHashId);
                preparedStatement.setString(4, runtimeProperty.getDescription());
                preparedStatement.setInt(5, tenantId);
                preparedStatement.setBoolean(6, runtimeProperty.isSecured());

                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();

        } catch (SQLException e) {
            String msg = "Error occurred while adding property to the database for version id : " + versionHashId + " in " +
                    "tenant : " + tenantId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);

        } finally {
            DBUtil.closePreparedStatement(preparedStatement);
        }

        return true;

    }


    public void addDeployment(Connection dbConnection, String versionHashId, Deployment deployment, int tenantId) throws AppCloudException {

        int deploymentId = addDeployment(dbConnection, deployment, tenantId);
        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.UPDATE_VERSION_WITH_DEPLOYMENT);
            preparedStatement.setInt(1, deploymentId);
            preparedStatement.setString(2, versionHashId);
            preparedStatement.setInt(3, tenantId);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            String msg = "Error while updating deployment detail for version with hash id : " + versionHashId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closePreparedStatement(preparedStatement);
        }
    }


    private int addDeployment(Connection dbConnection, Deployment deployment, int tenantId) throws AppCloudException {

        PreparedStatement preparedStatement = null;
        int deploymentId = -1;

        try {

            preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.ADD_DEPLOYMENT, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, deployment.getDeploymentName());
            preparedStatement.setInt(2, deployment.getReplicas());
            preparedStatement.setInt(3, tenantId);
            preparedStatement.execute();

            ResultSet rs = preparedStatement.getGeneratedKeys();

            if (rs.next()) {
                deploymentId = rs.getInt(1);
            }

            for (Container container : deployment.getContainers()) {
                addContainer(dbConnection, container, deploymentId, tenantId);
            }

        } catch (SQLException e) {
            String msg = "Error while inserting deployment record.";
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closePreparedStatement(preparedStatement);
        }
        if (deploymentId == -1) {
            throw new AppCloudException("Failed to insert deployment record.");
        }
        return deploymentId;
    }


    public void addContainer(Connection dbConnection, Container container, int deploymentId, int tenantId) throws AppCloudException {

        PreparedStatement preparedStatement = null;
        int containerId = -1;

        try {

            preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.ADD_CONTAINER, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, container.getImageName());
            preparedStatement.setString(2, container.getImageVersion());
            preparedStatement.setInt(3, deploymentId);
            preparedStatement.setInt(4, tenantId);

            preparedStatement.execute();

            ResultSet rs = preparedStatement.getGeneratedKeys();
            if (rs.next()) {
                containerId = rs.getInt(1);
            }
            for (ContainerServiceProxy containerServiceProxy : container.getServiceProxies()) {
                addContainerServiceProxy(dbConnection, containerServiceProxy, containerId, tenantId);
            }

        } catch (SQLException e) {
            String msg = "Error while inserting deployment container record.";
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closePreparedStatement(preparedStatement);
        }
    }

    public void addContainerServiceProxy(Connection dbConnection, ContainerServiceProxy containerServiceProxy,
                                         int containerId, int tenantId) throws AppCloudException {

        PreparedStatement preparedStatement = null;

        try {

            preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.ADD_CONTAINER_SERVICE_PROXY, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, containerServiceProxy.getServiceName());
            preparedStatement.setString(2, containerServiceProxy.getServiceProtocol());
            preparedStatement.setInt(3, containerServiceProxy.getServicePort());
            preparedStatement.setString(4, containerServiceProxy.getServiceBackendPort());
            preparedStatement.setInt(5, containerId);
            preparedStatement.setInt(6, tenantId);
            preparedStatement.setString(7, containerServiceProxy.getHostURL());
            preparedStatement.execute();

        } catch (SQLException e) {
            String msg = "Error while inserting container service proxy record.";
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closePreparedStatement(preparedStatement);
        }
    }


    public void updateApplicationIcon(Connection dbConnection, InputStream inputStream, int applicationId)
            throws AppCloudException {

        PreparedStatement preparedStatement = null;

        try {

            preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.UPDATE_APPLICATION_ICON);
            preparedStatement.setBlob(1, inputStream);
            preparedStatement.setInt(2, applicationId);
            preparedStatement.execute();

        } catch (SQLException e) {
            String msg =
                    "Error occurred while updating application icon for application with id : " + applicationId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);

        } finally {
            DBUtil.closePreparedStatement(preparedStatement);
        }
    }


    /**
     * Method for updating the status of the given version
     *
     * @param status        status of the version
     * @param versionHashId version hash id
     * @return
     * @throws AppCloudException
     */
    public boolean updateVersionStatus(Connection dbConnection, String status, String versionHashId)
            throws AppCloudException {

        PreparedStatement preparedStatement = null;

        try {

            preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.UPDATE_APPLICATION_STATUS);
            preparedStatement.setString(1, status);
            preparedStatement.setString(2, versionHashId);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            String msg = "Error while updating application status : " + status + " for version with the hash id : " +
                    versionHashId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closePreparedStatement(preparedStatement);
        }
        return true;
    }

    public void updateRuntimeProperty(Connection dbConnection, String versionHashId, String oldKey, String newKey,
                                      String newValue) throws AppCloudException {

        PreparedStatement preparedStatement = null;

        try {

            preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.UPDATE_RUNTIME_PROPERTIES);
            preparedStatement.setString(1, newKey);
            preparedStatement.setString(2, newValue);
            preparedStatement.setString(3, versionHashId);
            preparedStatement.setString(4, oldKey);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            String msg = "Error while updating runtime property Key : " + oldKey + " for version with hash id : " +
                    versionHashId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closePreparedStatement(preparedStatement);
        }
    }

    public void updateTag(Connection dbConnection, String versionHashId, String oldKey, String newKey, String newValue)
            throws AppCloudException {

        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.UPDATE_TAG);
            preparedStatement.setString(1, newKey);
            preparedStatement.setString(2, newValue);
            preparedStatement.setString(3, versionHashId);
            preparedStatement.setString(4, oldKey);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            String msg = "Error while updating tag Key : " + oldKey + " for version with hash id : " + versionHashId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closePreparedStatement(preparedStatement);
        }
    }


    /**
     * Method for getting the list of applications of a tenant from database with minimal information
     *
     * @param dbConnection database connection
     * @param tenantId     tenant id
     * @return
     * @throws AppCloudException
     */
    public List<Application> getAllApplicationsList(Connection dbConnection, int tenantId) throws AppCloudException {

        PreparedStatement preparedStatement = null;

        List<Application> applications = new ArrayList<>();
        Application application;
        ResultSet resultSet = null;

        try {

            preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.GET_ALL_APPLICATIONS_LIST);
            preparedStatement.setInt(1, tenantId);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {

                application = new Application();
                application.setApplicationName(resultSet.getString(SQLQueryConstants.APPLICATION_NAME));
                application.setApplicationType(resultSet.getString(SQLQueryConstants.APPLICATION_TYPE_NAME));
                application.setHashId(resultSet.getString(SQLQueryConstants.HASH_ID));
                application.setIcon(resultSet.getBlob(SQLQueryConstants.ICON));

                applications.add(application);
            }

        } catch (SQLException e) {
            String msg = "Error while retrieving application list from database for tenant : " + tenantId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closeResultSet(resultSet);
            DBUtil.closePreparedStatement(preparedStatement);
        }
        return applications;
    }

    /**
     * Method for getting the list of applications of a tenant from database with tag details
     *
     * @param dbConnection database connection
     * @param tenantId     tenant id
     * @return
     * @throws AppCloudException
     */
    public List<Application> getTaggedApplicationsList(Connection dbConnection, int tenantId) throws AppCloudException {

        PreparedStatement preparedStatement = null;

        List<Application> applications = new ArrayList<>();
        Application application;
        ResultSet resultSet = null;
        Boolean applicationExists = false;

        try {

            preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.GET_ALL_APPLICATIONS_LIST_WITH_TAG);
            preparedStatement.setInt(1, tenantId);

            resultSet = preparedStatement.executeQuery();

            List<Version> versions = new ArrayList<>();
            Version version;
            List<Tag> tags = new ArrayList<>();
            Tag tag;

            while (resultSet.next()) {

                applicationExists = false;
                for (Application applicationTemp : applications) {
                    if (applicationTemp.getHashId().equals(resultSet.getString(SQLQueryConstants.HASH_ID))) {
                        applicationExists = true;
                        tag = new Tag();
                        tag.setTagName(resultSet.getString(SQLQueryConstants.TAG_KEY));
                        tag.setTagValue(resultSet.getString(SQLQueryConstants.TAG_VALUE));
                        applicationTemp.getVersions().get(0).getTags().add(tag);
                    }
                }
                if (!applicationExists) {
                    tag = new Tag();
                    tag.setTagName(resultSet.getString(SQLQueryConstants.TAG_KEY));
                    tag.setTagValue(resultSet.getString(SQLQueryConstants.TAG_VALUE));
                    tags.add(tag);

                    version = new Version();
                    version.setTags(tags);
                    versions.add(version);

                    application = new Application();
                    application.setApplicationName(resultSet.getString(SQLQueryConstants.APPLICATION_NAME));
                    application.setApplicationType(resultSet.getString(SQLQueryConstants.APPLICATION_TYPE_NAME));
                    application.setHashId(resultSet.getString(SQLQueryConstants.HASH_ID));
                    application.setIcon(resultSet.getBlob(SQLQueryConstants.ICON));
                    application.setVersions(versions);

                    applications.add(application);
                }

            }

        } catch (SQLException e) {
            String msg = "Error while retrieving application list from database for tenant : " + tenantId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closeResultSet(resultSet);
            DBUtil.closePreparedStatement(preparedStatement);
        }
        return applications;
    }


    public List<String> getAllVersionListOfApplication(Connection dbConnection, String applicationHashId)
            throws AppCloudException {

        PreparedStatement preparedStatement = null;
        ArrayList<String> versionList = new ArrayList<>();
        ResultSet resultSet = null;

        try {

            preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.GET_VERSION_LIST_OF_APPLICATION);
            preparedStatement.setString(1, applicationHashId);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                versionList.add(resultSet.getString(SQLQueryConstants.NAME));
            }

        } catch (SQLException e) {
            String msg = "Error while getting the list of versions for the application with hash id : " + applicationHashId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closeResultSet(resultSet);
            DBUtil.closePreparedStatement(preparedStatement);
        }
        return versionList;
    }


    public List<String> getAllVersionHashIdsOfApplication(Connection dbConnection, String applicationHashId)
            throws AppCloudException {

        PreparedStatement preparedStatement = null;
        ArrayList<String> hashIdList = new ArrayList<>();
        ResultSet resultSet = null;

        try {
            preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.GET_VERSION_HASH_IDS_OF_APPLICATION);
            preparedStatement.setString(1, applicationHashId);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                hashIdList.add(resultSet.getString(SQLQueryConstants.HASH_ID));
            }

        } catch (SQLException e) {
            String msg = "Error while getting the list of version hash ids of application : " + applicationHashId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closeResultSet(resultSet);
            DBUtil.closePreparedStatement(preparedStatement);
        }

        return hashIdList;
    }


    public boolean isSingleVersion(Connection dbConnection, String versionHashId) throws AppCloudException {

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.GET_VERSION_HASH_IDS_OF_APPLICATION_BY_VERSION_HASH_ID);
            preparedStatement.setString(1, versionHashId);

            resultSet = preparedStatement.executeQuery();
            resultSet.last();

            if (resultSet.getRow() > 1) {
                return false;
            } else {
                return true;
            }

        } catch (SQLException e) {
            String msg = "Error while retrieving the data for checking whether an application has multiple versions";
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closeResultSet(resultSet);
            DBUtil.closePreparedStatement(preparedStatement);
        }
    }

    public String getApplicationHashIdByVersionHashId(Connection dbConnection, String versionHashId)
            throws AppCloudException {

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String applicatinHashId = null;

        try {
            preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.GET_APPLICATION_HASH_ID_BY_VERSION_HASH_ID);
            preparedStatement.setString(1, versionHashId);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                applicatinHashId = resultSet.getString(SQLQueryConstants.HASH_ID);
            }

        } catch (SQLException e) {
            String msg = "Error while getting application hash id by version hash id : " + versionHashId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closeResultSet(resultSet);
            DBUtil.closePreparedStatement(preparedStatement);
        }
        return applicatinHashId;
    }


    public String getApplicationNameByHashId(Connection dbConnection, String applicationHashId)
            throws AppCloudException {
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        String applicationName = null;

        try {

            preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.GET_APPLICATION_NAME_BY_HASH_ID);
            preparedStatement.setString(1, applicationHashId);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                applicationName = resultSet.getString(SQLQueryConstants.NAME);
            }

        } catch (SQLException e) {
            String msg = "Error while getting the application name of application with hash id : " + applicationHashId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        }

        return applicationName;
    }


    public String getApplicationHashIdByName(Connection dbConnection, String applicationName, int tenantId)
            throws AppCloudException {

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String applicationHashId = null;

        try {
            preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.GET_APPLICATION_HASH_ID_BY_NAME);
            preparedStatement.setString(1, applicationName);
            preparedStatement.setInt(2, tenantId);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                applicationHashId = resultSet.getString(SQLQueryConstants.HASH_ID);
            }

        } catch (SQLException e) {
            String msg = "Error while retrieving application hash id using application name : " + applicationName +
                    " in tenant : " + tenantId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closeResultSet(resultSet);
            DBUtil.closePreparedStatement(preparedStatement);
        }

        return applicationHashId;
    }

    /**
     * Method for getting application from database using application hash id
     *
     * @param dbConnection      database connection
     * @param applicationHashId application hash id
     * @return
     * @throws AppCloudException
     */
    public Application getApplicationByHashId(Connection dbConnection, String applicationHashId) throws AppCloudException {

        PreparedStatement preparedStatement = null;
        Application application = new Application();
        ResultSet resultSet = null;

        try {

            preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.GET_APPLICATION_BY_HASH_ID);
            preparedStatement.setString(1, applicationHashId);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {

                application.setApplicationName(resultSet.getString(SQLQueryConstants.NAME));
                application.setHashId(applicationHashId);
                application.setDescription(resultSet.getString(SQLQueryConstants.DESCRIPTION));
                application.setDefaultVersion(resultSet.getString(SQLQueryConstants.DEFAULT_VERSION));
                application.setApplicationType(resultSet.getString(SQLQueryConstants.APPLICATION_TYPE_NAME));
                application.setIcon(resultSet.getBlob(SQLQueryConstants.ICON));
                application.setVersions(getAllVersionsOfApplication(dbConnection, applicationHashId));

            }

        } catch (SQLException e) {
            String msg =
                    "Error while retrieving application detail for application with hash id : " + applicationHashId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closeResultSet(resultSet);
            DBUtil.closePreparedStatement(preparedStatement);
        }
        return application;
    }

    /**
     * Method for retrieving all the versions of a specific application
     *
     * @param dbConnection
     * @param applicationHashId
     * @return
     * @throws AppCloudException
     */
    public List<Version> getAllVersionsOfApplication(Connection dbConnection, String applicationHashId)
            throws AppCloudException {

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<Version> versions = new ArrayList<>();
        try {

            preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.GET_ALL_VERSIONS_OF_APPLICATION);
            preparedStatement.setString(1, applicationHashId);

            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {

                Version version = new Version();
                version.setVersionName(resultSet.getString(SQLQueryConstants.NAME));
                version.setHashId(resultSet.getString(SQLQueryConstants.HASH_ID));
                version.setRuntimeName(resultSet.getString(SQLQueryConstants.RUNTIME_NAME));
                version.setRuntimeId(resultSet.getInt(SQLQueryConstants.RUNTIME_ID));
                version.setStatus(resultSet.getString(SQLQueryConstants.STATUS));
                version.setConSpecCpu(resultSet.getString(SQLQueryConstants.CON_SPEC_CPU));
                version.setConSpecMemory(resultSet.getString((SQLQueryConstants.CON_SPEC_MEMORY)));
                version.setTags(getAllTagsOfVersion(dbConnection, version.getHashId()));
                version.setRuntimeProperties(getAllRuntimePropertiesOfVersion(dbConnection, version.getHashId()));

                versions.add(version);
            }

        } catch (SQLException e) {
            String msg = "Error while getting all versions of application with application hash id : " + applicationHashId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closeResultSet(resultSet);
            DBUtil.closePreparedStatement(preparedStatement);
        }

        return versions;
    }


    /**
     * Method for retrieving list of labels belongs to a given version of an application
     *
     * @param dbConnection  database connection
     * @param versionHashId version hash id
     * @return
     * @throws AppCloudException
     */
    public List<Tag> getAllTagsOfVersion(Connection dbConnection, String versionHashId) throws AppCloudException {

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<Tag> tags = new ArrayList<>();
        try {

            preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.GET_ALL_TAGS_OF_VERSION);
            preparedStatement.setString(1, versionHashId);

            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {

                Tag tag = new Tag();
                tag.setTagName(resultSet.getString(SQLQueryConstants.NAME));
                tag.setTagValue(resultSet.getString(SQLQueryConstants.VALUE));
                tag.setDescription(resultSet.getString(SQLQueryConstants.DESCRIPTION));

                tags.add(tag);
            }

        } catch (SQLException e) {
            String msg = "Error while retrieving tags from database for version with hash id : " + versionHashId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closeResultSet(resultSet);
            DBUtil.closePreparedStatement(preparedStatement);
        }

        return tags;
    }


    /**
     * Method for retrieving all the runtime properties of a given version of an application
     *
     * @param dbConnection  database connection
     * @param versionHashId version hash id
     * @return
     * @throws AppCloudException
     */
    public List<RuntimeProperty> getAllRuntimePropertiesOfVersion(Connection dbConnection, String versionHashId)
            throws AppCloudException {

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<RuntimeProperty> runtimeProperties = new ArrayList<>();

        try {

            preparedStatement = dbConnection.prepareStatement(
                    SQLQueryConstants.GET_ALL_RUNTIME_PROPERTIES_OF_VERSION);
            preparedStatement.setString(1, versionHashId);

            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {

                RuntimeProperty runtimeProperty = new RuntimeProperty();
                runtimeProperty.setPropertyName(resultSet.getString(SQLQueryConstants.NAME));
                runtimeProperty.setPropertyValue(resultSet.getString(SQLQueryConstants.VALUE));
                runtimeProperty.setDescription(resultSet.getString(SQLQueryConstants.DESCRIPTION));
                runtimeProperty.setSecured(resultSet.getBoolean(SQLQueryConstants.IS_SECURED));

                runtimeProperties.add(runtimeProperty);
            }

        } catch (SQLException e) {
            String msg = "Error while retrieving the runtime properties from the database for version with hash id : " +
                    versionHashId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closeResultSet(resultSet);
            DBUtil.closePreparedStatement(preparedStatement);
        }

        return runtimeProperties;
    }


    /**
     * Method for getting the id of an application with the given hash id
     *
     * @param dbConnection      database connection
     * @param applicationHashId application hash id
     * @return
     * @throws AppCloudException
     */
    public int getApplicationId(Connection dbConnection, String applicationHashId) throws AppCloudException {

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        int applicationId = 0;

        try {

            preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.GET_APPLICATION_ID);
            preparedStatement.setString(1, applicationHashId);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                applicationId = resultSet.getInt(SQLQueryConstants.ID);
            }

        } catch (SQLException e) {
            String msg = "Error while retrieving the id of application with hash value : " + applicationHashId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closeResultSet(resultSet);
            DBUtil.closePreparedStatement(preparedStatement);
        }

        return applicationId;
    }


    /**
     * Method for getting the version id with the given hash id
     *
     * @param dbConnection
     * @param hashId
     * @return
     * @throws AppCloudException
     */
    public int getVersionId(Connection dbConnection, String hashId) throws AppCloudException {

        PreparedStatement preparedStatement;
        int versionId = 0;

        try {

            preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.GET_VERSION_ID);
            preparedStatement.setString(1, hashId);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                versionId = resultSet.getInt(SQLQueryConstants.ID);
            }

        } catch (SQLException e) {
            String msg = "Error while retreiving id of version with hash value : " + hashId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        }
        return versionId;
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

        List<ApplicationType> applicationTypeList = new ArrayList<>();
        ApplicationType applicationType;

        try {

            preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.GET_ALL_APP_TYPES);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {

                applicationType = new ApplicationType();
                applicationType.setAppTypeName(resultSet.getString(SQLQueryConstants.NAME));
                applicationType.setDescription(resultSet.getString(SQLQueryConstants.DESCRIPTION));
                applicationType.setBuildable(resultSet.getInt(SQLQueryConstants.BUILDABLE) == 1 ? true : false);
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
                applicationRuntime.setRuntimeName(resultSet.getString(SQLQueryConstants.NAME));
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


    public ApplicationRuntime getRuntimeById(int runtimeId) throws AppCloudException {

        Connection dbConnection = DBUtil.getDBConnection();
        PreparedStatement preparedStatement = null;
        ApplicationRuntime applicationRuntime = new ApplicationRuntime();
        ResultSet resultSet = null;

        try {

            preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.GET_RUNTIME_BY_ID);
            preparedStatement.setInt(1, runtimeId);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                applicationRuntime.setId(resultSet.getInt(SQLQueryConstants.ID));
                applicationRuntime.setImageName(resultSet.getString(SQLQueryConstants.RUNTIME_IMAGE_NAME));
                applicationRuntime.setRepoURL(resultSet.getString(SQLQueryConstants.RUNTIME_REPO_URL));
                applicationRuntime.setRuntimeName(resultSet.getString(SQLQueryConstants.NAME));
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

    public Deployment getDeployment(String versionHashId) throws AppCloudException {

        Connection dbConnection = DBUtil.getDBConnection();
        PreparedStatement preparedStatement = null;
        Deployment deployment = new Deployment();

        try {

            preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.GET_DEPLOYMENT);
            preparedStatement.setString(1, versionHashId);

            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                deployment.setDeploymentName(rs.getString(SQLQueryConstants.NAME));
                deployment.setReplicas(rs.getInt(SQLQueryConstants.REPLICAS));
                deployment.setContainers(getContainers(rs.getInt(SQLQueryConstants.ID), versionHashId));
            }

            dbConnection.commit();

        } catch (SQLException e) {
            String msg = "Error while inserting deployment record.";
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closePreparedStatement(preparedStatement);
            DBUtil.closeConnection(dbConnection);
        }

        return deployment;
    }


    public Set<Container> getContainers(int deploymentId, String versionHashId) throws AppCloudException {

        Connection dbConnection = DBUtil.getDBConnection();
        PreparedStatement preparedStatement = null;
        Set<Container> containers = new HashSet<>();

        try {

            preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.GET_CONTAINER);
            preparedStatement.setInt(1, deploymentId);

            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {

                Container container = new Container();
                container.setImageName(rs.getString(SQLQueryConstants.NAME));
                container.setImageVersion(rs.getString(SQLQueryConstants.VERSION));
                container.setServiceProxies(getContainerServiceProxies(rs.getInt(SQLQueryConstants.ID)));

                List<RuntimeProperty> runtimeProperties = getAllRuntimePropertiesOfVersion(dbConnection, versionHashId);
                container.setRuntimeProperties(runtimeProperties);
                containers.add(container);

            }
            dbConnection.commit();

        } catch (SQLException e) {
            String msg = "Error while inserting deployment container record.";
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closePreparedStatement(preparedStatement);
            DBUtil.closeConnection(dbConnection);
        }
        return containers;
    }


    private Set<ContainerServiceProxy> getContainerServiceProxies(int containerId) throws AppCloudException {

        Connection dbConnection = DBUtil.getDBConnection();
        PreparedStatement preparedStatement = null;
        Set<ContainerServiceProxy> containerServiceProxies = new HashSet<>();

        try {

            preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.GET_CONTAINER_SERVICE_PROXIES);
            preparedStatement.setInt(1, containerId);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                ContainerServiceProxy containerServiceProxy = new ContainerServiceProxy();
                containerServiceProxy.setServiceName(resultSet.getString(SQLQueryConstants.NAME));
                containerServiceProxy.setServiceProtocol(resultSet.getString(SQLQueryConstants.PROTOCOL));
                containerServiceProxy.setServicePort(resultSet.getInt(SQLQueryConstants.PORT));
                containerServiceProxy.setServiceBackendPort(resultSet.getString(SQLQueryConstants.BACKEND_PORT));
                containerServiceProxy.setHostURL(resultSet.getString(SQLQueryConstants.HOST_URL));
                containerServiceProxies.add(containerServiceProxy);
            }

            dbConnection.commit();

        } catch (SQLException e) {
            String msg = "Error while inserting deployment service proxy record.";
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closePreparedStatement(preparedStatement);
            DBUtil.closeConnection(dbConnection);
        }
        return containerServiceProxies;
    }


    public List<Transport> getTransportsForRuntime(int runtimeId) throws AppCloudException {

        Connection dbConnection = DBUtil.getDBConnection();
        PreparedStatement preparedStatement = null;
        List<Transport> transports = new ArrayList<>();

        try {

            preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.GET_TRANSPORTS_FOR_RUNTIME);
            preparedStatement.setInt(1, runtimeId);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Transport transport = new Transport();
                transport.setServiceName(resultSet.getString(SQLQueryConstants.NAME));
                transport.setServiceProtocol(resultSet.getString(SQLQueryConstants.PROTOCOL));
                transport.setServicePort(resultSet.getInt(SQLQueryConstants.PORT));
                transport.setServiceNamePrefix(resultSet.getString(SQLQueryConstants.SERVICE_NAME_PREFIX));
                transports.add(transport);
            }
            dbConnection.commit();

        } catch (SQLException e) {
            String msg = "Error while retrieving runtime transport detail for runtime : " + runtimeId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closePreparedStatement(preparedStatement);
            DBUtil.closeConnection(dbConnection);
        }
        return transports;
    }


    public boolean deleteRuntimeProperty(Connection dbConnection, String versionHashId, String key) throws AppCloudException {

        PreparedStatement preparedStatement = null;
        boolean deleted = false;
        try {

            preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.DELETE_RUNTIME_PROPERTY);
            preparedStatement.setString(1, versionHashId);
            preparedStatement.setString(2, key);

            deleted = preparedStatement.execute();

        } catch (SQLException e) {
            String msg = "Error while deleting runtime property Key : " + key + " for version with hash id : " +
                    versionHashId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closePreparedStatement(preparedStatement);
        }
        return deleted;
    }


    public boolean deleteTag(Connection dbConnection, String versionHashId, String key) throws AppCloudException {

        PreparedStatement preparedStatement = null;
        boolean deleted = false;
        try {

            preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.DELETE_TAG);
            preparedStatement.setString(1, versionHashId);
            preparedStatement.setString(2, key);

            deleted = preparedStatement.execute();

        } catch (SQLException e) {
            String msg = "Error while deleting tag with Key : " + key + " for version with hash id : " + versionHashId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closePreparedStatement(preparedStatement);
        }
        return deleted;
    }


    /**
     * Delete an application.
     *
     * @param applicationHashId application hash id
     * @return
     * @throws AppCloudException
     */
    public boolean deleteApplication(Connection dbConnection, String applicationHashId)
            throws AppCloudException {
        PreparedStatement preparedStatement = null;
        boolean deleted = false;

        try {
            preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.DELETE_APPLICATION);
            preparedStatement.setString(1, applicationHashId);

            deleted = preparedStatement.execute();
        } catch (SQLException e) {
            String msg = "Error while executing the application deletion sql query with applicationHashId : " + applicationHashId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closePreparedStatement(preparedStatement);
        }
        return deleted;
    }

    public boolean deleteVersion(Connection dbConnection, String versionHashId) throws AppCloudException {
        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.DELETE_VERSION);
            preparedStatement.setString(1, versionHashId);

            return preparedStatement.execute();
        } catch (SQLException e) {
            String msg = "Error while executing the version deletion sql query with versionHashId : " + versionHashId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closePreparedStatement(preparedStatement);
        }
    }

    /**
     * Delete all the versions of an application
     *
     * @param applicationHashId application hash id
     * @return
     * @throws AppCloudException
     */
    public boolean deleteAllVersionsOfApplication(Connection dbConnection, String applicationHashId) throws AppCloudException {

        PreparedStatement preparedStatement = null;
        boolean deleted = false;
        try {

            preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.DELETE_VERSIONS_OF_APPLICATION);
            preparedStatement.setString(1, applicationHashId);

            deleted = preparedStatement.execute();

        } catch (SQLException e) {
            String msg = "Error while deleting the versions of the application with hash id : " + applicationHashId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closePreparedStatement(preparedStatement);
        }
        return deleted;
    }

    public void deleteAllDeploymentOfApplication(Connection dbConnection, String applicationHashId)
            throws AppCloudException {

        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.DELETE_ALL_DEPLOYMENT_OF_APPLICATION);
            preparedStatement.setString(1, applicationHashId);

            preparedStatement.execute();

        } catch (SQLException e) {
            String msg = "Error while deleting all deployment of application with hash id : " + applicationHashId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closePreparedStatement(preparedStatement);
        }

    }


    public void deleteDeployment(Connection dbConnection, String versionHashId) throws AppCloudException {

        PreparedStatement preparedStatement = null;

        try {

            preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.DELETE_DEPLOYMENT);
            preparedStatement.setString(1, versionHashId);

            preparedStatement.execute();

        } catch (SQLException e) {
            String msg = "Error while deleting deployment record for version with the hash id : " + versionHashId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closePreparedStatement(preparedStatement);
        }
    }

    public int getApplicationCount(int tenantId) throws AppCloudException {
        Connection dbConnection = DBUtil.getDBConnection();
        PreparedStatement preparedStatement = null;
        int appCount = 0;
        try {
            preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.GET_TENANT_APPLICATION_COUNT);
            preparedStatement.setInt(1, tenantId);
            ResultSet rs = preparedStatement.executeQuery();
            dbConnection.commit();
            if (rs.next()) {
                appCount = rs.getInt(1);
            }
        } catch (SQLException e) {
            String msg = "Error while getting the application count of the tenant : " + tenantId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closePreparedStatement(preparedStatement);
        }
        return appCount;
    }

    /**
     * Get service proxy for given version
     *
     * @param versionHashId
     * @return
     * @throws AppCloudException
     */
    public List<ContainerServiceProxy> getContainerServiceProxyByVersion(String versionHashId)
            throws AppCloudException {
        Connection dbConnection = DBUtil.getDBConnection();
        PreparedStatement preparedStatement = null;
        List<ContainerServiceProxy> containerServiceProxies = new ArrayList<ContainerServiceProxy>();

        try {
            preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.GET_CONTAINER_SERVICE_PROXY);
            preparedStatement.setString(1, versionHashId);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                ContainerServiceProxy containerServiceProxy = new ContainerServiceProxy();
                containerServiceProxy.setServiceName(rs.getString(SQLQueryConstants.NAME));
                containerServiceProxy.setServiceProtocol(rs.getString(SQLQueryConstants.PROTOCOL));
                containerServiceProxy.setServicePort(rs.getInt(SQLQueryConstants.PORT));
                containerServiceProxy.setServiceBackendPort(rs.getString(SQLQueryConstants.BACKEND_PORT));
                containerServiceProxy.setHostURL(rs.getString(SQLQueryConstants.HOST_URL));
                containerServiceProxies.add(containerServiceProxy);
            }

            dbConnection.commit();
        } catch (SQLException e) {
            String msg = "Error while getting container service proxy with version hash id : " + versionHashId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closePreparedStatement(preparedStatement);
            DBUtil.closeConnection(dbConnection);
        }
        return containerServiceProxies;
    }

    /**
     * Update host url from container service proxy for custom url
     *
     * @param dbConnection
     * @param versionHashId
     * @param host_url
     * @return
     * @throws AppCloudException
     */
    public boolean updateContainerServiceProxy(Connection dbConnection, String versionHashId, String host_url)
            throws AppCloudException {
        PreparedStatement preparedStatement = null;
        boolean success = false;

        try {
            preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.UPDATE_CONTAINER_SERVICE_PROXY);
            preparedStatement.setString(1, host_url);
            preparedStatement.setString(2, versionHashId);
            success = preparedStatement.execute();
        } catch (SQLException e) {
            String msg =
                    "Error occurred while updating container service proxy with version hash id : " + versionHashId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closePreparedStatement(preparedStatement);
        }

        return success;
    }

    /**
     * Update default version for given application
     *
     * @param applicationHashId
     * @return if sucessfully update the default version
     * @throws AppCloudException
     */
    public boolean updateDefaultVersion(Connection dbConnection, String applicationHashId, String defaultVersionName)
            throws AppCloudException {
        PreparedStatement preparedStatement = null;
        boolean updated = false;

        try {
            preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.UPDATE_APPLICATION_DEFAULT_VERSION);
            preparedStatement.setString(1, defaultVersionName);
            preparedStatement.setString(2, applicationHashId);
            updated = preparedStatement.execute();
        } catch (SQLException e) {
            String message = "Error while updating default version with application hash id : " + applicationHashId;
            log.error(message, e);
            throw new AppCloudException(message, e);
        } finally {
            DBUtil.closePreparedStatement(preparedStatement);
        }

        return updated;
    }


    public Version[] getApplicationVersionsByRunningTimePeriod(int numberOfHours) throws AppCloudException {
        Connection dbConnection = DBUtil.getDBConnection();
        PreparedStatement preparedStatement = null;
        List<Version> versions = new ArrayList<>();

        try {

            preparedStatement = dbConnection.prepareStatement(
                    SQLQueryConstants.GET_ALL_APP_VERSIONS_CREATED_BEFORE_X_DAYS_AND_NOT_WHITE_LISTED);
            preparedStatement.setInt(1, numberOfHours);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Version version = new Version();
                version.setHashId(resultSet.getString(SQLQueryConstants.HASH_ID));
                version.setCreatedTimestamp(resultSet.getTimestamp(SQLQueryConstants.EVENT_TIMESTAMP));
                version.setTenantId(resultSet.getInt(SQLQueryConstants.TENANT_ID));

                versions.add(version);
            }
            dbConnection.commit();


        } catch (SQLException e) {
            String msg = "Error while retrieving application version detail for non white listed applications.";
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closePreparedStatement(preparedStatement);
            DBUtil.closeConnection(dbConnection);
        }
        return versions.toArray(new Version[versions.size()]);
    }
}
