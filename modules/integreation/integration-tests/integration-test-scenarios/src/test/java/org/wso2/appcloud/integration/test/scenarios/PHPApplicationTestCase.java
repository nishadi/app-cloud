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

package org.wso2.appcloud.integration.test.scenarios;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.wso2.appcloud.integration.test.utils.AppCloudIntegrationTestConstants;
import org.wso2.appcloud.integration.test.utils.AppCloudIntegrationTestUtils;
import org.wso2.appcloud.integration.test.utils.clients.ApplicationClient;

public class PHPApplicationTestCase extends ApplicationTestCase {

	private static final Log log = LogFactory.getLog(PHPApplicationTestCase.class);

	@BeforeClass(alwaysRun = true)
	public void createApplication() throws Exception {
		applicationClient = new ApplicationClient(serverUrl, defaultAdmin, defaultAdminPassword);
		String runtimeID = AppCloudIntegrationTestUtils.getPropertyValue(AppCloudIntegrationTestConstants.PHP_APP_RUNTIME_ID_KEY);
		String fileName = AppCloudIntegrationTestUtils.getPropertyValue(AppCloudIntegrationTestConstants.PHP_APP_FILE_NAME_KEY);
		createApplication(runtimeID, fileName);
	}

//	@SetEnvironment(executionEnvironments = { ExecutionEnvironment.PLATFORM})
//    @Test(description = "Create war application using rest api")
//    public void testTomcatWARApplication() throws Exception {
//		//Application details
//		String applicationName = AppCloudIntegrationTestUtils.getPropertyValue(AppCloudIntegrationTestConstants.APP_NAME_KEY);
//		String runtimeID = AppCloudIntegrationTestUtils.getPropertyValue(AppCloudIntegrationTestConstants.PHP_APP_RUNTIME_ID_KEY);
//		String applicationType = AppCloudIntegrationTestUtils.getPropertyValue(AppCloudIntegrationTestConstants.APP_TYPE_KEY);
//		String applicationRevision = AppCloudIntegrationTestUtils.getPropertyValue(AppCloudIntegrationTestConstants.APP_REVISION_KEY);
//		String applicationDescription = AppCloudIntegrationTestUtils.getPropertyValue(AppCloudIntegrationTestConstants.APP_DESC_KEY);
//		String fileName = AppCloudIntegrationTestUtils.getPropertyValue(
//				AppCloudIntegrationTestConstants.PHP_APP_FILE_NAME_KEY);
//		String properties = AppCloudIntegrationTestUtils.getKeyValuePairAsJson(
//				AppCloudIntegrationTestUtils.getPropertyNodes(AppCloudIntegrationTestConstants.APP_PROPERTIES_KEY));
//		String tags = AppCloudIntegrationTestUtils.getKeyValuePairAsJson(
//				AppCloudIntegrationTestUtils.getPropertyNodes(AppCloudIntegrationTestConstants.APP_TAGS_KEY));
//		File uploadArtifact = new File(TestConfigurationProvider.getResourceLocation() + fileName);
//
//		//Application creation
//		ApplicationClient applicationClient = new ApplicationClient(serverUrl, defaultAdmin, defaultAdminPassword);
//		applicationClient.createNewApplication(applicationName, runtimeID, applicationType, applicationRevision,
//		                                       applicationDescription, fileName, properties, tags, uploadArtifact);
//
//		//Wait until creation finished
//		long timeOutPeriod = AppCloudIntegrationTestUtils.getTimeOutPeriod();
//		int retryCount = AppCloudIntegrationTestUtils.getTimeOutRetryCount();
//		int round = 1;
//		while(round <= retryCount) {
//			try {
//				JSONObject result = applicationClient.getApplicationEvents(applicationName, applicationRevision);
//				Assert.assertEquals("Application creation failed", AppCloudIntegrationTestConstants.STATUS_RUNNING,
//				                    result.getString(AppCloudIntegrationTestConstants.PROPERTY_STATUS_NAME));
//				break;
//			} catch (Exception e) {
//				Thread.sleep(timeOutPeriod);
//				round++;
//			}
//		}
//
//		//Delete application
//		boolean isDeleted = applicationClient.deleteApplication(applicationName);
//		Assert.assertEquals("Application deletion failed", isDeleted, true);
//    }

	@AfterClass(alwaysRun = true)
	public void deleteApplication() throws Exception {
		String applicationName = AppCloudIntegrationTestUtils.getPropertyValue(AppCloudIntegrationTestConstants.APP_NAME_KEY);
		boolean isDeleted = applicationClient.deleteApplication(applicationName);
		Assert.assertEquals("Application deletion failed", isDeleted, true);
	}

}
