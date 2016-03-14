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
import org.json.JSONObject;
import org.junit.Assert;
import org.testng.annotations.Test;
import org.wso2.appcloud.integration.test.utils.AppCloudIntegrationTestConstants;
import org.wso2.appcloud.integration.test.utils.AppCloudIntegrationTestUtils;
import org.wso2.appcloud.integration.test.utils.AppCloudIntegrationTests;
import org.wso2.appcloud.integration.test.utils.clients.ApplicationClient;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.test.utils.common.TestConfigurationProvider;

import java.io.File;

public class MSF4JApplicationTestCase extends AppCloudIntegrationTests {

	private static final Log log = LogFactory.getLog(MSF4JApplicationTestCase.class);

	@SetEnvironment(executionEnvironments = { ExecutionEnvironment.PLATFORM})
    @Test(description = "Create war application using rest api")
    public void testCreateTomcatWARApplication() throws Exception {
		//Application details
		String applicationName = "msf4j";
		String runtimeID = "2";
		String applicationType = "mss";
		String applicationRevision = "1.0.0";
		String applicationDescription = "sample msf4j application";
		String fileName = "sample.war";
		String properties = "[]";
		String tags = "[]";
		File uploadArtifact = new File(TestConfigurationProvider.getResourceLocation() + fileName);

		//Application creation
		ApplicationClient applicationClient = new ApplicationClient(serverUrl, defaultAdmin, defaultAdminPassword);
		applicationClient.createNewApplication(applicationName, runtimeID, applicationType, applicationRevision,
		                                       applicationDescription, fileName, properties, tags, uploadArtifact);

		//Wait until creation finished
		long timeOutPeriod = AppCloudIntegrationTestUtils.getTimeOutPeriod();
		int retryCount = AppCloudIntegrationTestUtils.getTimeOutRetryCount();
		int round = 1;
		while(round <= retryCount) {
			try {
				JSONObject result = applicationClient.getApplicationEvents(applicationName, applicationRevision);
				Assert.assertEquals("Application creation failed", AppCloudIntegrationTestConstants.STATUS_RUNNING,
				                    result.getString(AppCloudIntegrationTestConstants.PROPERTY_STATUS_NAME));
				break;
			} catch (Exception e) {
				Thread.sleep(timeOutPeriod);
				round++;
			}
		}

		//TODO: Application Deletion
    }

}
