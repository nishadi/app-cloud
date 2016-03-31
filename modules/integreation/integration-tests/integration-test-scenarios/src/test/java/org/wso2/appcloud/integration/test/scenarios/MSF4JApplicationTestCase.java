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
import org.wso2.appcloud.integration.test.utils.AppCloudIntegrationTestConstants;
import org.wso2.appcloud.integration.test.utils.AppCloudIntegrationTestUtils;

public class MSF4JApplicationTestCase extends AppCloudIntegrationBaseTestCase {

	private static final Log log = LogFactory.getLog(MSF4JApplicationTestCase.class);
	public static final String MSS_SERVER_STARTED_MESSAGE = "Microservices server started in";
	public static final String MSF4J_APPLICATION_TYPE = "mss";

	public MSF4JApplicationTestCase(){
		super(AppCloudIntegrationTestUtils.getPropertyValue(AppCloudIntegrationTestConstants.MSS_APP_RUNTIME_ID_KEY),
		      AppCloudIntegrationTestUtils.getPropertyValue(AppCloudIntegrationTestConstants.MSS_APP_FILE_NAME_KEY),
		      MSF4J_APPLICATION_TYPE);
	}

	@Override
	protected void assertLogContent(String logContent) {
		Assert.assertTrue("Container haven't started up", logContent.contains(MSS_SERVER_STARTED_MESSAGE));
	}
}
