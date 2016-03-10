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
import org.testng.annotations.Test;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.wso2.appcloud.integration.test.utils.AppCloudIntegrationTestConstants;
import org.wso2.appcloud.integration.test.utils.AppCloudIntegrationTestUtils;
import org.wso2.appcloud.integration.test.utils.AppCloudIntegrationTests;
import org.wso2.appcloud.integration.test.utils.clients.ApplicationClient;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.test.utils.common.TestConfigurationProvider;

import java.io.File;

public class ApplicationCreationTestCase extends AppCloudIntegrationTests {

	private static final Log log = LogFactory.getLog(ApplicationCreationTestCase.class);

    @SetEnvironment(executionEnvironments = { ExecutionEnvironment.PLATFORM})
    @Test(description = "Create application using rest api")
    public void testCreateApplication() throws Exception {

	    String applicationName = AppCloudIntegrationTestUtils.getPropertyValue(
			    AppCloudIntegrationTestConstants.DEFAULT_APP_APP_NAME);
	    String runtimeID = AppCloudIntegrationTestUtils.getPropertyValue(
			    AppCloudIntegrationTestConstants.DEFAULT_APP_APP_RUNTIME_NAME);
	    String applicationType = AppCloudIntegrationTestUtils.getPropertyValue(
			    AppCloudIntegrationTestConstants.DEFAULT_APP_APP_TYPE);
	    String version = AppCloudIntegrationTestUtils.getPropertyValue(
			    AppCloudIntegrationTestConstants.DEFAULT_APP_APP_VERSION);
	    String applicationDescription = AppCloudIntegrationTestUtils.getPropertyValue(
			    AppCloudIntegrationTestConstants.DEFAULT_APP_APP_DESC);
	    String fileName = AppCloudIntegrationTestUtils.getPropertyValue(
			    AppCloudIntegrationTestConstants.DEFAULT_APP_APP_FILE_NAME);
	    String properties = getKeyValuePairAsJson(AppCloudIntegrationTestUtils.getPropertyNodes(
			    AppCloudIntegrationTestConstants.DEFAULT_APP_APP_PROPERTIES));
	    String tags = getKeyValuePairAsJson(AppCloudIntegrationTestUtils.getPropertyNodes(
			    AppCloudIntegrationTestConstants.DEFAULT_APP_APP_TAGS));
	    String resourcePath = AppCloudIntegrationTestUtils.getPropertyValue(
			    AppCloudIntegrationTestConstants.DEFAULT_APP_ARTIFACT_PATH);

	    File uploadArtifact = new File(TestConfigurationProvider.getResourceLocation() + resourcePath);
        ApplicationClient applicationClient = new ApplicationClient(serverUrl, defaultAdmin, defaultAdminPassword);
        applicationClient.createNewApplication(applicationName, runtimeID, applicationType, version,
                                               applicationDescription, fileName, properties, tags, uploadArtifact);
        //log.info("###########################33");
    }

	/**
	 * [{key="key1",value=value1},...]
	 * @param propertyNodes
	 * @return
	 */
	private String getKeyValuePairAsJson(NodeList propertyNodes) {
		StringBuilder result = new StringBuilder("[");
		for (int i = 0 ; i < propertyNodes.getLength() ; i++) {
			if(i != 0){
				result.append(",");
			}
			Element element = (Element)propertyNodes.item(i);
			String key = element.getAttribute(AppCloudIntegrationTestConstants.ATTRIBUTE_KEY);
			String value = element.getTextContent();
			result.append("{\"key\":\"");
			result.append(key);
			result.append("\",\"value\":\"");
			result.append(value);
			result.append("\"}");
		}
		result.append("]");
		return result.toString();
	}
}
