package org.wso2.appcloud.integration.test.scenarios;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.junit.Assert;
import org.wso2.appcloud.integration.test.utils.AppCloudIntegrationTestConstants;
import org.wso2.appcloud.integration.test.utils.AppCloudIntegrationTestUtils;
import org.wso2.appcloud.integration.test.utils.AppCloudIntegrationTests;
import org.wso2.appcloud.integration.test.utils.clients.ApplicationClient;
import org.wso2.carbon.automation.test.utils.common.TestConfigurationProvider;

import java.io.File;

/**
 * Basic test case to implement things common to all app types.
 */
public abstract class ApplicationTestCase extends AppCloudIntegrationTests {

	private static final Log log = LogFactory.getLog(ApplicationTestCase.class);
	protected ApplicationClient applicationClient;

	protected void createApplication(String runtimeID, String fileName) throws Exception {
		//Application details
		String applicationName =
				AppCloudIntegrationTestUtils.getPropertyValue(AppCloudIntegrationTestConstants.APP_NAME_KEY);
		String applicationType =
				AppCloudIntegrationTestUtils.getPropertyValue(AppCloudIntegrationTestConstants.APP_TYPE_KEY);
		String applicationRevision =
				AppCloudIntegrationTestUtils.getPropertyValue(AppCloudIntegrationTestConstants.APP_REVISION_KEY);
		String applicationDescription =
				AppCloudIntegrationTestUtils.getPropertyValue(AppCloudIntegrationTestConstants.APP_DESC_KEY);
		String properties = AppCloudIntegrationTestUtils.getKeyValuePairAsJson(
				AppCloudIntegrationTestUtils.getPropertyNodes(AppCloudIntegrationTestConstants.APP_PROPERTIES_KEY));
		String tags = AppCloudIntegrationTestUtils.getKeyValuePairAsJson(
				AppCloudIntegrationTestUtils.getPropertyNodes(AppCloudIntegrationTestConstants.APP_TAGS_KEY));
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
	}

}
