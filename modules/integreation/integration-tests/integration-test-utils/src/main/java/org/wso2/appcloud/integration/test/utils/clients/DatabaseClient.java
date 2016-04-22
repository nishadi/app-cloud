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
package org.wso2.appcloud.integration.test.utils.clients;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.commons.httpclient.HttpStatus;
import org.json.JSONObject;
import org.wso2.appcloud.integration.test.utils.AppCloudIntegrationTestConstants;
import org.wso2.appcloud.integration.test.utils.AppCloudIntegrationTestException;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;

import java.util.HashMap;
import java.util.Map;

public class DatabaseClient extends BaseClient {

    private final String CREATE_DATABASE_ACTION = "createDatabaseAndAttachUser";
    private final String CREATE_DATABASE_USER_ACTION = "createDatabaseUser";
    private final String ATTACH_USER_TO_DATABASE_ACTION = "attachUserWithPermissions";
    private final String DETACH_USER_FROM_DATABASE_ACTION = "detachUser";
    private final String DELETE_USER_ACTION = "deleteUser";
    private final String DELETE_DATABASE_ACTION = "dropDatabase";
    private final String GET_DATABASE_INFO_ACTION = "getDatabaseInfoForDataTable";
    private final String GET_DATABASE_USERS_INFO_ACTION = "getDatabaseUsersForDataTable";

    private final String DATABASE_NAME = "databaseName";
    private final String CUSTOM_PASSWORD = "customPassword";
    //TODO change dbName to databaseName in  blocks/database/add/ajax/add.jag getDatabaseUsersForDataTable
    private final String DB_NAME = "dbName";
    //TODO change username to userName in blocks/database/users/ajax/list.jag createDatabaseUser and detachUser
    private final String USER_NAME = "username";
    private final String PASSWORD = "password";
    private final String IS_BASIC = "isBasic";
    //TODO change name to userName in deleteUser
    private final String NAME = "name";


    /**
     * Construct authenticates REST client to invoke appmgt functions
     *
     * @param backEndUrl backend url
     * @param username   username
     * @param password   password
     * @throws Exception
     */
    public DatabaseClient(String backEndUrl, String username, String password) throws Exception {
        super(backEndUrl, username, password);
    }

    /**
     * Create new database , create new user and attach user to database.
     *
     * @param databaseName
     * @param userName
     * @param customPassword
     * @param isBasic
     * @throws AppCloudIntegrationTestException
     */
    public JSONObject createDatabaseAndAttachUser(String databaseName, String userName, String customPassword, String
            isBasic) throws AppCloudIntegrationTestException {

        Map<String, String> msgBodyMap = new HashMap<String, String>();
        msgBodyMap.put(PARAM_NAME_ACTION, CREATE_DATABASE_ACTION);
        msgBodyMap.put(DATABASE_NAME, databaseName);
        msgBodyMap.put(PARAM_NAME_USER_NAME, userName);
        msgBodyMap.put(CUSTOM_PASSWORD, customPassword);
        msgBodyMap.put(IS_BASIC, isBasic);
        HttpResponse response = super.doPostRequest(AppCloudIntegrationTestConstants.DATABASE_ADD, msgBodyMap);
        if (response.getResponseCode() == HttpStatus.SC_OK) {
            JSONObject jsonObject = new JSONObject(response.getData());
            return jsonObject;
        } else {
            throw new AppCloudIntegrationTestException(
                    "Error occurred while creating database " + response.getResponseCode() + response.getData());
        }

    }

    /**
     * This method will return a json element with all the database details.
     *
     * @return
     * @throws AppCloudIntegrationTestException
     */
    public JsonElement getDatabases() throws AppCloudIntegrationTestException {


        Map<String, String> msgBodyMap = new HashMap<String, String>();
        msgBodyMap.put(PARAM_NAME_ACTION, GET_DATABASE_INFO_ACTION);
        HttpResponse response = super.doPostRequest(AppCloudIntegrationTestConstants.DATABASE_LIST, msgBodyMap);
        if (response.getResponseCode() == HttpStatus.SC_OK) {
            JsonParser jsonParser = new JsonParser();
            JsonElement jsonElement = jsonParser.parse(response.getData());
            return jsonElement;
        } else {
            throw new AppCloudIntegrationTestException("Error while getting databases " + response.getResponseCode()
                                                       + response.getData());
        }
    }

    /**
     * This will return all the database users in the system, categorized in to two :
     * Attached users and Available users to attach - to the given database name.
     *
     * @param databaseName
     * @return
     * @throws AppCloudIntegrationTestException
     */
    public JsonElement getDatabaseUsers(String databaseName) throws AppCloudIntegrationTestException {


        Map<String, String> msgBodyMap = new HashMap<String, String>();
        msgBodyMap.put(PARAM_NAME_ACTION, GET_DATABASE_USERS_INFO_ACTION);
        msgBodyMap.put(DB_NAME, databaseName);
        HttpResponse response = super.doPostRequest(AppCloudIntegrationTestConstants.DATABASE_ADD, msgBodyMap);
        if (response.getResponseCode() == HttpStatus.SC_OK) {
            JsonParser jsonParser = new JsonParser();
            JsonElement jsonElement = jsonParser.parse(response.getData());
            return jsonElement;
        } else {
            throw new AppCloudIntegrationTestException("Error while getting database users " +
                                                       response.getResponseCode() + response.getData());
        }
    }

    /**
     * Create new database user.
     *
     * @param userName
     * @param password
     * @throws AppCloudIntegrationTestException
     */
    public JSONObject createDatabaseUser(String userName, String password) throws AppCloudIntegrationTestException {
        Map<String, String> msgBodyMap = new HashMap<String, String>();
        msgBodyMap.put(PARAM_NAME_ACTION, CREATE_DATABASE_USER_ACTION);
        msgBodyMap.put(USER_NAME, userName);
        msgBodyMap.put(PASSWORD, password);
        HttpResponse response = super.doPostRequest(AppCloudIntegrationTestConstants.DATABASE_USERS, msgBodyMap);
        if (response.getResponseCode() == HttpStatus.SC_OK) {
            JSONObject jsonObject = new JSONObject(response.getData());
            return jsonObject;
        } else {
            throw new AppCloudIntegrationTestException(
                    "Error occurred while creating database user" + response.getResponseCode() + response.getData());
        }


    }

    /**
     * Attach a user to a database.
     *
     * @param databaseName
     * @param userName
     * @throws AppCloudIntegrationTestException
     */
    public void attachUserToDatabase(String databaseName, String userName) throws AppCloudIntegrationTestException {
        Map<String, String> msgBodyMap = new HashMap<String, String>();
        msgBodyMap.put(PARAM_NAME_ACTION, ATTACH_USER_TO_DATABASE_ACTION);
        msgBodyMap.put(PARAM_NAME_USER_NAME, userName);
        msgBodyMap.put(DATABASE_NAME, databaseName);
        HttpResponse response = super.doPostRequest(AppCloudIntegrationTestConstants.DATABASE_ADD, msgBodyMap);
        if (response.getResponseCode() == HttpStatus.SC_OK) {
            return;
        } else {
            throw new AppCloudIntegrationTestException(
                    "Error occurred when attaching user to database" + response.getResponseCode() + response.getData());
        }
    }

    /**
     * Detach a user from database.
     *
     * @param databaseName
     * @param userName
     * @throws AppCloudIntegrationTestException
     */
    public void detachUserFromDatabase(String databaseName, String userName) throws AppCloudIntegrationTestException {
        Map<String, String> msgBodyMap = new HashMap<String, String>();
        msgBodyMap.put(PARAM_NAME_ACTION, DETACH_USER_FROM_DATABASE_ACTION);
        msgBodyMap.put(USER_NAME, userName);
        msgBodyMap.put(DATABASE_NAME, databaseName);
        HttpResponse response = super.doPostRequest(AppCloudIntegrationTestConstants.DATABASE_ADD, msgBodyMap);
        if (response.getResponseCode() == HttpStatus.SC_OK) {
            return;
        } else {
            throw new AppCloudIntegrationTestException(
                    "Error occurred when detaching user." + response.getResponseCode() + response.getData());
        }
    }

    /**
     * Delete a database user from system. User should be detached from all dbs.
     * @param userName
     * @throws AppCloudIntegrationTestException
     */
    public void deleteDatabaseUser(String userName) throws AppCloudIntegrationTestException {
        Map<String, String> msgBodyMap = new HashMap<String, String>();
        msgBodyMap.put(PARAM_NAME_ACTION, DELETE_USER_ACTION);
        msgBodyMap.put(NAME, userName);
        HttpResponse response = super.doPostRequest(AppCloudIntegrationTestConstants.DATABASE_USERS, msgBodyMap);
        if (response.getResponseCode() == HttpStatus.SC_OK) {
            return;
        } else {
            throw new AppCloudIntegrationTestException(
                    "Error occurred when deleting user." + response.getResponseCode() + response.getData());
        }

    }

    /**
     * Delete a database from system.
     * Users attached to db will be automatically detached and users will NOT be deleted by this method.
     * @param databaseName
     * @throws AppCloudIntegrationTestException
     */
    public void deleteDatabase(String databaseName) throws AppCloudIntegrationTestException {
        Map<String, String> msgBodyMap = new HashMap<String, String>();
        msgBodyMap.put(PARAM_NAME_ACTION, DELETE_DATABASE_ACTION);
        msgBodyMap.put(DATABASE_NAME, databaseName);
        HttpResponse response = super.doPostRequest(AppCloudIntegrationTestConstants.DATABASE_DROP, msgBodyMap);
        if (response.getResponseCode() == HttpStatus.SC_OK) {
            return;
        } else {
            throw new AppCloudIntegrationTestException(
                    "Error occurred when deleting database." + response.getResponseCode() + response.getData());
        }
    }


}
