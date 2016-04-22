/*
 * Copyright 2016 WSO2, Inc. (http://wso2.com)
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.appcloud.integration.test.scenarios;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appcloud.integration.test.utils.AppCloudIntegrationTestConstants;
import org.wso2.appcloud.integration.test.utils.AppCloudIntegrationTestException;
import org.wso2.appcloud.integration.test.utils.AppCloudIntegrationTestUtils;
import org.wso2.appcloud.integration.test.utils.clients.DatabaseClient;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;

/**
 * Test case for testing all database related operations in app cloud
 */
public class DatabaseTestCase {

    private DatabaseClient databaseClient;
    private String defaultAdmin;
    private String defaultAdminPassword;
    private String serverUrl;
    private String dbName = "db1";
    private String dbUserName = "sanga";
    private String dbUserName2 = "mayya";
    private String dbUserPassword = "admin123";


    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        serverUrl = AppCloudIntegrationTestUtils.getPropertyValue(AppCloudIntegrationTestConstants.URLS_APPCLOUD);
        defaultAdmin = AppCloudIntegrationTestUtils.getAdminUsername();
        defaultAdminPassword = AppCloudIntegrationTestUtils.getAdminPassword();
        databaseClient = new DatabaseClient(serverUrl, defaultAdmin, defaultAdminPassword);
    }


    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.PLATFORM})
    @Test(description = "Checking whether database already exists")
    public void testCheckDatabaseAlreadyExists() throws AppCloudIntegrationTestException {
        Assert.assertEquals(databaseClient.getDatabases()
                                          .toString().contains(dbName), false, "Database already exists!");
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.PLATFORM})
    @Test(description = "Create database", dependsOnMethods = {"testCheckDatabaseAlreadyExists"})
    public void testCreateDatabase() throws AppCloudIntegrationTestException {
        databaseClient.createDatabaseAndAttachUser(dbName, dbUserName, dbUserPassword, AppCloudIntegrationTestConstants
                .TRUE_STRING);
        Assert.assertEquals(databaseClient.getDatabases()
                                          .toString().contains(dbName), true, "Creating database not success.");
        Assert.assertEquals(databaseClient.getDatabases()
                                          .toString().contains(dbUserName), true, "Database user is not attached");
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.PLATFORM})
    @Test(description = "Checking whether user already exists", dependsOnMethods = {"testCreateDatabase"})
    public void testCheckUserAlreadyExists() throws AppCloudIntegrationTestException {
        Assert.assertEquals(databaseClient.getDatabaseUsers(dbName)
                                          .toString().contains(dbUserName2), false, "Database user already exists!");
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.PLATFORM})
    @Test(description = "Create new database user", dependsOnMethods = {"testCheckUserAlreadyExists"})
    public void testCreateNewDatabaseUser() throws AppCloudIntegrationTestException {
        databaseClient.createDatabaseUser(dbUserName2, dbUserPassword);
        Assert.assertEquals(databaseClient.getDatabaseUsers(dbName)
                                          .toString().contains(dbUserName2), true, "Database user creation failed");
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.PLATFORM})
    @Test(description = "Attaching User For the initially created database", dependsOnMethods =
            {"testCreateNewDatabaseUser"})
    public void testAttachUserForDatabase() throws AppCloudIntegrationTestException {
        databaseClient.attachUserToDatabase(dbName, dbUserName2);
        Assert.assertEquals(databaseClient.getDatabases()
                                          .toString().contains(dbUserName2), true, "Database user attaching failed");
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.PLATFORM})
    @Test(description = "Detaching the user from database", dependsOnMethods =
            {"testAttachUserForDatabase"})
    public void testDetachUserFromDatabase() throws AppCloudIntegrationTestException {
        databaseClient.detachUserFromDatabase(dbName, dbUserName2);
        Assert.assertEquals(databaseClient.getDatabases()
                                          .toString().contains(dbUserName2), false, "User detaching failed");
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.PLATFORM})
    @Test(description = "Deleting database user", dependsOnMethods =
            {"testDetachUserFromDatabase"})
    public void testDeleteDatabaseUser() throws AppCloudIntegrationTestException {
        databaseClient.deleteDatabaseUser(dbUserName2);
        Assert.assertEquals(databaseClient.getDatabaseUsers(dbName)
                                          .toString().contains(dbUserName2), false, "Database user deletion failed");
    }

    @SetEnvironment(executionEnvironments = {ExecutionEnvironment.PLATFORM})
    @Test(description = "Deleting database", dependsOnMethods =
            {"testDeleteDatabaseUser"})
    public void testDeleteDatabase() throws AppCloudIntegrationTestException {
        databaseClient.deleteDatabase(dbName);
        Assert.assertEquals(databaseClient.getDatabases()
                                          .toString().contains(dbName), false, "Database deletion failed");
    }

    @AfterClass(alwaysRun = true)
    public void cleanEnvironment() throws Exception {
        databaseClient.detachUserFromDatabase(dbName, dbUserName);
        databaseClient.detachUserFromDatabase(dbName, dbUserName2);
        databaseClient.deleteDatabaseUser(dbUserName);
        databaseClient.deleteDatabaseUser(dbUserName2);
        databaseClient.deleteDatabase(dbName);

    }
}
