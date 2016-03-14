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
	public static final String DEFAULT_APP_APP_NAME = "//appCloudProperties/defaultApplication/applicationName";
	public static final String DEFAULT_APP_APP_KEY = "//appCloudProperties/defaultApplication/applicationKey";
	public static final String DEFAULT_APP_APP_TYPE = "//appCloudProperties/defaultApplication/applicationType";
	public static final String DEFAULT_APP_APP_DESC = "//appCloudProperties/defaultApplication/applicationDescription";
	public static final String DEFAULT_APP_APP_REVISION = "//appCloudProperties/defaultApplication/defaultRevision";
	public static final String DEFAULT_APP_APP_RUNTIME_ID = "//appCloudProperties/defaultApplication/runtimeID";
	public static final String DEFAULT_APP_APP_FILE_NAME = "//appCloudProperties/defaultApplication/filename";
	public static final String DEFAULT_APP_APP_PROPERTIES = "//appCloudProperties/defaultApplication/properties/property";
	public static final String DEFAULT_APP_APP_TAGS = "//appCloudProperties/defaultApplication/tags/tag";
	public static final String DEFAULT_APP_ARTIFACT_PATH = "//appCloudProperties/defaultApplication/resource_path";

	// Rest endpoints
	public static final String REST_APPLICATION_ENDPOINT = "application/application.jag";
	public static final String APPMGT_USER_LOGIN = "user/login/ajax/login.jag";
	public static final String APPMGT_URL_SURFIX = "appmgt/site/blocks/";

	public static final String RESPONSE_MESSAGE_NAME = "message";
	public static final String ATTRIBUTE_KEY = "key";
	public static final String STATUS_RUNNING = "running";
	public static final String PROPERTY_STATUS_NAME = "status";
}
