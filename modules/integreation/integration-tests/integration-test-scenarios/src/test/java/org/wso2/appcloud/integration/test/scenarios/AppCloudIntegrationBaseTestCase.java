package org.wso2.appcloud.integration.test.scenarios;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.junit.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appcloud.integration.test.utils.AppCloudIntegrationTestConstants;
import org.wso2.appcloud.integration.test.utils.AppCloudIntegrationTestUtils;
import org.wso2.appcloud.integration.test.utils.clients.ApplicationClient;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.test.utils.common.TestConfigurationProvider;

import java.io.File;

/**
 * Basic test case to implement things common to all app types.
 */
public abstract class AppCloudIntegrationBaseTestCase {

	private static final Log log = LogFactory.getLog(AppCloudIntegrationBaseTestCase.class);
	protected String defaultAdmin;
	protected String defaultAdminPassword;
	protected String defaultAppName;
	protected String serverUrl;
	protected String tenantDomain;
	private String fileName;
	private String runtimeID;
	private ApplicationClient applicationClient;
	protected String applicationName;
	protected String applicationType;
	protected String applicationRevision;
	protected String applicationDescription;
	protected String properties;
	protected String tags;
	
	public AppCloudIntegrationBaseTestCase(String runtimeID, String fileName){
		this.runtimeID = runtimeID;
		this.fileName = fileName;
		//Application details
		this.applicationName = AppCloudIntegrationTestUtils.getPropertyValue(AppCloudIntegrationTestConstants.APP_NAME_KEY);
		this.applicationType = AppCloudIntegrationTestUtils.getPropertyValue(AppCloudIntegrationTestConstants.APP_TYPE_KEY);
		this.applicationRevision  = AppCloudIntegrationTestUtils.getPropertyValue(AppCloudIntegrationTestConstants.APP_REVISION_KEY);
		this.applicationDescription = AppCloudIntegrationTestUtils.getPropertyValue(AppCloudIntegrationTestConstants.APP_DESC_KEY);
		this.properties = AppCloudIntegrationTestUtils.getKeyValuePairAsJson(
				AppCloudIntegrationTestUtils.getPropertyNodes(AppCloudIntegrationTestConstants.APP_PROPERTIES_KEY));
		this.tags = AppCloudIntegrationTestUtils.getKeyValuePairAsJson(
				AppCloudIntegrationTestUtils.getPropertyNodes(AppCloudIntegrationTestConstants.APP_TAGS_KEY));
	}

	@BeforeClass(alwaysRun = true)
	public void setEnvironment() throws Exception {
		defaultAdmin = AppCloudIntegrationTestUtils.getAdminUsername();
		defaultAdminPassword = AppCloudIntegrationTestUtils.getAdminPassword();
		defaultAppName = AppCloudIntegrationTestUtils.getPropertyValue(AppCloudIntegrationTestConstants.APP_NAME_KEY);
		serverUrl = AppCloudIntegrationTestUtils.getPropertyValue(AppCloudIntegrationTestConstants.URLS_APPCLOUD);
		tenantDomain =  AppCloudIntegrationTestUtils.getPropertyValue(AppCloudIntegrationTestConstants.DEFAULT_TENANT_TENANT_DOMAIN);

		applicationClient = new ApplicationClient(serverUrl, defaultAdmin, defaultAdminPassword);
		createApplication();
	}
	
	public void createApplication() throws Exception {
		//Load the file in resources
		File uploadArtifact = new File(TestConfigurationProvider.getResourceLocation() + fileName);
		//Application creation
		ApplicationClient applicationClient = new ApplicationClient(serverUrl, defaultAdmin, defaultAdminPassword);
		applicationClient.createNewApplication(applicationName, this.runtimeID, applicationType, applicationRevision,
		                                       applicationDescription, this.fileName, properties, tags, uploadArtifact);

		//Wait until creation finished
		long timeOutPeriod = AppCloudIntegrationTestUtils.getTimeOutPeriod();
		int retryCount = AppCloudIntegrationTestUtils.getTimeOutRetryCount();
		int round = 1;
		while(round <= retryCount) {
			try {
				JSONObject result = applicationClient.getApplicationBean(applicationName, applicationRevision);
				log.info("Application current status is : " + result.getString(AppCloudIntegrationTestConstants.PROPERTY_STATUS_NAME));
				Assert.assertEquals("Application creation failed", AppCloudIntegrationTestConstants.STATUS_RUNNING,
				                    result.getString(AppCloudIntegrationTestConstants.PROPERTY_STATUS_NAME));
				break;
			} catch (Exception e) {
				Thread.sleep(timeOutPeriod);
				round++;
			}
		}
	}

	@SetEnvironment(executionEnvironments = { ExecutionEnvironment.PLATFORM})
	@Test(description = "Testing stop application action")
	public void stopApplication() throws Exception {
		applicationClient.stopApplicationRevision(applicationName, applicationRevision);

		//Wait until stop application finished
		long timeOutPeriod = AppCloudIntegrationTestUtils.getTimeOutPeriod();
		int retryCount = AppCloudIntegrationTestUtils.getTimeOutRetryCount();
		int round = 1;
		while(round <= retryCount) {
			try {
				JSONObject result = applicationClient.getApplicationBean(applicationName, applicationRevision);
				log.info("Application current status is : " + result.getString(AppCloudIntegrationTestConstants.PROPERTY_STATUS_NAME));
				Assert.assertEquals("Application stop action failed", AppCloudIntegrationTestConstants.STATUS_STOPPED,
				                    result.getString(AppCloudIntegrationTestConstants.PROPERTY_STATUS_NAME));
				break;
			} catch (Exception e) {
				Thread.sleep(timeOutPeriod);
				round++;
			}
		}
	}

	@SetEnvironment(executionEnvironments = { ExecutionEnvironment.PLATFORM})
	@Test(description = "Testing stop application action")
	public void startApplication() throws Exception {
		applicationClient.startApplicationRevision(applicationName, applicationRevision);

		//Wait until start application finished
		long timeOutPeriod = AppCloudIntegrationTestUtils.getTimeOutPeriod();
		int retryCount = AppCloudIntegrationTestUtils.getTimeOutRetryCount();
		int round = 1;
		while(round <= retryCount) {
			try {
				JSONObject result = applicationClient.getApplicationBean(applicationName, applicationRevision);
				log.info("Application current status is : " + result.getString(AppCloudIntegrationTestConstants.PROPERTY_STATUS_NAME));
				Assert.assertEquals("Application start action failed", AppCloudIntegrationTestConstants.STATUS_RUNNING,
				                    result.getString(AppCloudIntegrationTestConstants.PROPERTY_STATUS_NAME));
				break;
			} catch (Exception e) {
				Thread.sleep(timeOutPeriod);
				round++;
			}
		}
	}


	@AfterClass(alwaysRun = true)
	public void cleanEnvironment() throws Exception {
		String applicationName = AppCloudIntegrationTestUtils.getPropertyValue(AppCloudIntegrationTestConstants.APP_NAME_KEY);
		boolean isDeleted = applicationClient.deleteApplication(applicationName);
		Assert.assertEquals("Application deletion failed", isDeleted, true);
	}
}
