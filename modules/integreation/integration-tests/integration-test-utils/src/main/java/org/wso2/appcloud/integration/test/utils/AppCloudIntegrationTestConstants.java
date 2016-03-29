/*
 *   Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   WSO2 Inc. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */

package org.wso2.appcloud.integration.test.utils;

public class AppCloudIntegrationTestConstants {

    // Product groups
    public static final String APPCLOUD_PRODUCT_GROUP = "AppCloud";

	// Automation Config Paths
    public static final String DEFAULT_TENANT_ADMIN = "//appCloudProperties/defaultTenant/admin";
    public static final String DEFAULT_TENANT_ADMIN_PASSWORD = "//appCloudProperties/defaultTenant/adminPassword";
    public static final String DEFAULT_TENANT_TENANT_DOMAIN = "//appCloudProperties/defaultTenant/tenantDomain";
    public static final String TIMEOUT_PERIOD = "//appCloudProperties/timeOutSetting/period";
    public static final String TIMEOUT_RETRY_COUNT = "//appCloudProperties/timeOutSetting/retryCount";
    public static final String URLS_APPCLOUD = "//appCloudProperties/urls/appCloud";

	// Default Application Details
	public static final String APP_NAME_KEY = "//appCloudProperties/application/applicationName";
	public static final String APP_DESC_KEY = "//appCloudProperties/application/applicationDescription";
	public static final String APP_REVISION_KEY = "//appCloudProperties/application/defaultRevision";
	public static final String APP_NEW_REVISION_KEY = "//appCloudProperties/application/newRevision";
	public static final String TOMCAT_APP_RUNTIME_ID_KEY = "//appCloudProperties/application/runtimeIDs/runtimeID[@key='tomcat']";
	public static final String MSS_APP_RUNTIME_ID_KEY = "//appCloudProperties/application/runtimeIDs/runtimeID[@key='msf4j']";
	public static final String PHP_APP_RUNTIME_ID_KEY = "//appCloudProperties/application/runtimeIDs/runtimeID[@key='php']";
	public static final String TOMCAT_APP_FILE_NAME_KEY = "//appCloudProperties/application/files/file[@type='war']";
	public static final String MSS_APP_FILE_NAME_KEY = "//appCloudProperties/application/files/file[@type='msf4j']";
	public static final String PHP_APP_FILE_NAME_KEY = "//appCloudProperties/application/files/file[@type='php']";
	public static final String APP_PROPERTIES_KEY = "//appCloudProperties/application/properties/property";
	public static final String APP_NEW_PROPERTIES_KEY = "//appCloudProperties/application/newProperties/property";
	public static final String APP_TAGS_KEY = "//appCloudProperties/application/tags/tag";
	public static final String APP_NEW_TAGS_KEY = "//appCloudProperties/application/newTags/tag";

	// Rest endpoints
	public static final String REST_APPLICATION_ENDPOINT = "application/application.jag";
	public static final String REST_LOGS_ENDPOINT = "runtimeLogs/ajax/runtimeLogs.jag";
	public static final String APPMGT_USER_LOGIN = "user/login/ajax/login.jag";
	public static final String APPMGT_URL_SURFIX = "appmgt/site/blocks/";

	public static final String RESPONSE_MESSAGE_NAME = "message";
	public static final String ATTRIBUTE_KEY = "key";
	public static final String STATUS_RUNNING = "running";
	public static final String STATUS_STOPPED = "stopped";
	public static final String PROPERTY_STATUS_NAME = "status";
	public static final String PROPERTY_VERSIONS_NAME = "versions";
}
