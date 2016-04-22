package org.wso2.appcloud.integration.test.scenarios;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appcloud.integration.test.utils.AppCloudIntegrationTestConstants;
import org.wso2.appcloud.integration.test.utils.AppCloudIntegrationTestUtils;
import org.wso2.appcloud.integration.test.utils.clients.ApplicationClient;
import org.wso2.appcloud.integration.test.utils.clients.LogsClient;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.test.utils.common.TestConfigurationProvider;

import java.io.File;
import java.util.Map;

/**
 * Basic test case to implement things common to all app types.
 */
public abstract class AppCloudIntegrationBaseTestCase {

	private static final Log log = LogFactory.getLog(AppCloudIntegrationBaseTestCase.class);
	public static final String PARAM_NAME_KEY = "key";
	public static final String PARAM_NAME_VALUE = "value";
	protected String defaultAdmin;
	protected String defaultAdminPassword;
	protected String defaultAppName;
	protected String serverUrl;
	protected String tenantDomain;
	private String fileName;
	private String runtimeID;
	private ApplicationClient applicationClient;
	private LogsClient logsClient;
	protected String applicationName;
	protected String applicationType;
	protected String applicationRevision;
	protected String applicationDescription;
	protected String properties;
	protected String tags;
	private String containerSpecMemory = "1024";
	private String containerSpecCpu = "300";

	public AppCloudIntegrationBaseTestCase(String runtimeID, String fileName, String applicationType){
		this.runtimeID = runtimeID;
		this.fileName = fileName;
		//Application details
		this.applicationName = AppCloudIntegrationTestUtils.getPropertyValue(AppCloudIntegrationTestConstants.APP_NAME_KEY);
		this.applicationType = applicationType;
		this.applicationRevision  = AppCloudIntegrationTestUtils.getPropertyValue(AppCloudIntegrationTestConstants.APP_REVISION_KEY);
		this.applicationDescription = AppCloudIntegrationTestUtils.getPropertyValue(AppCloudIntegrationTestConstants.APP_DESC_KEY);
		this.properties = AppCloudIntegrationTestUtils.getKeyValuePairAsJsonFromConfig(
				AppCloudIntegrationTestUtils.getPropertyNodes(AppCloudIntegrationTestConstants.APP_PROPERTIES_KEY));
		this.tags = AppCloudIntegrationTestUtils.getKeyValuePairAsJsonFromConfig(
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
		logsClient = new LogsClient(serverUrl, defaultAdmin, defaultAdminPassword);

		createApplication();
	}

	public void createApplication() throws Exception {
		//Application creation
		File uploadArtifact = new File(TestConfigurationProvider.getResourceLocation() + fileName);
		applicationClient.createNewApplication(applicationName, this.runtimeID, applicationType, applicationRevision,
		                                       applicationDescription, this.fileName, properties, tags,
		                                       uploadArtifact, false, containerSpecMemory, containerSpecCpu);

		//Wait until creation finished
		RetryApplicationActions(applicationRevision, AppCloudIntegrationTestConstants.STATUS_RUNNING, "Application creation");
	}


	@SetEnvironment(executionEnvironments = { ExecutionEnvironment.PLATFORM})
	@Test(description = "Testing stop application action")
	public void testStopApplication() throws Exception {
		String versionHash = applicationClient.getVersionHash(applicationName, applicationRevision);
		applicationClient.stopApplicationRevision(applicationName, applicationRevision, versionHash);

		//Wait until stop application finished
		RetryApplicationActions(applicationRevision, AppCloudIntegrationTestConstants.STATUS_STOPPED, "Application stop action");
	}

	@SetEnvironment(executionEnvironments = { ExecutionEnvironment.PLATFORM})
	@Test(description = "Testing start application action", dependsOnMethods = {"testStopApplication"})
	public void testStartApplication() throws Exception {
		String versionHash = applicationClient.getVersionHash(applicationName, applicationRevision);
        applicationClient.startApplicationRevision(applicationName, applicationRevision, versionHash,
                                                   containerSpecMemory, containerSpecCpu);

		//Wait until start application finished
		RetryApplicationActions(applicationRevision, AppCloudIntegrationTestConstants.STATUS_RUNNING,
		                        "Application start action");
	}

	@SetEnvironment(executionEnvironments = { ExecutionEnvironment.PLATFORM})
	@Test(description = "Testing add runtime properties", dependsOnMethods = {"testStartApplication"})
	public void testAddEnvironmentalVariables() throws Exception {
		String versionHash = applicationClient.getVersionHash(applicationName, applicationRevision);
		Map<String, String> properties = AppCloudIntegrationTestUtils.getKeyValuePairsFromConfig(
				AppCloudIntegrationTestUtils.getPropertyNodes(AppCloudIntegrationTestConstants.APP_NEW_PROPERTIES_KEY));
		for (String key : properties.keySet()) {
			applicationClient.addRuntimeProperty(versionHash, key, properties.get(key));
		}
		JSONArray jsonArray = applicationClient.getRuntimeProperties(versionHash);
		int i = 0;
		for (Object object : jsonArray) {
			JSONObject jsonObject = (JSONObject)object;
			if(properties.containsKey(jsonObject.getString(PARAM_NAME_KEY))){
				i++;
				Assert.assertEquals("Value of the property doesn't match.", properties.get(jsonObject.getString(PARAM_NAME_KEY)),
				                    jsonObject.getString(PARAM_NAME_VALUE));
			}
		}
		Assert.assertTrue("One or more Properties are not added.", i == properties.size());
	}

	@SetEnvironment(executionEnvironments = { ExecutionEnvironment.PLATFORM})
	@Test(description = "Testing update runtime properties", dependsOnMethods = {"testAddEnvironmentalVariables"})
	public void testUpdateEnvironmentalVariables() throws Exception {
		String versionHash = applicationClient.getVersionHash(applicationName, applicationRevision);
		JSONArray jsonArray = applicationClient.getRuntimeProperties(versionHash);
		JSONObject jsonObject = (JSONObject)jsonArray.get(0);
		String prevKey = jsonObject.getString(PARAM_NAME_KEY);
		String newKey = RandomStringUtils.random(5, true, false);
		String newValue = RandomStringUtils.random(6, true, false);
		applicationClient.updateRuntimeProperty(versionHash, prevKey, newKey, newValue);
		JSONArray updatedJSONArray = applicationClient.getRuntimeProperties(versionHash);
		boolean containsNewKey = false;
		for (Object object : updatedJSONArray) {
			JSONObject jsonOBJ = (JSONObject)object;
			if(newKey.equals(jsonOBJ.getString(PARAM_NAME_KEY))){
				containsNewKey = true;
				Assert.assertEquals("Value of the property doesn't match.", newValue, jsonOBJ.getString(PARAM_NAME_VALUE));
			}
		}
		Assert.assertTrue("Property is not updated.", containsNewKey);
	}

	@SetEnvironment(executionEnvironments = { ExecutionEnvironment.PLATFORM})
	@Test(description = "Testing update runtime properties", dependsOnMethods = {"testUpdateEnvironmentalVariables"})
	public void testDeleteEnvironmentalVariables() throws Exception {
		String versionHash = applicationClient.getVersionHash(applicationName, applicationRevision);
		JSONArray jsonArray = applicationClient.getRuntimeProperties(versionHash);
		JSONObject jsonObject = (JSONObject)jsonArray.get(0);
		String key = jsonObject.getString(PARAM_NAME_KEY);
		applicationClient.deleteRuntimeProperty(versionHash, key);
		JSONArray updatedJSONArray = applicationClient.getRuntimeProperties(versionHash);
		boolean containsKey = false;
		for (Object object : updatedJSONArray) {
			JSONObject jsonOBJ = (JSONObject)object;
			if(key.equals(jsonOBJ.getString(PARAM_NAME_KEY))){
				containsKey = true;
			}
		}
		Assert.assertNotEquals("Property is not deleted.", containsKey);
	}

	@SetEnvironment(executionEnvironments = { ExecutionEnvironment.PLATFORM})
	@Test(description = "Testing add tags", dependsOnMethods = {"testDeleteEnvironmentalVariables"})
	public void testAddTags() throws Exception {
		String versionHash = applicationClient.getVersionHash(applicationName, applicationRevision);
		Map<String, String> properties = AppCloudIntegrationTestUtils.getKeyValuePairsFromConfig(
				AppCloudIntegrationTestUtils.getPropertyNodes(AppCloudIntegrationTestConstants.APP_NEW_TAGS_KEY));
		for (String key : properties.keySet()) {
			applicationClient.addTag(versionHash, key, properties.get(key));
		}
		JSONArray jsonArray = applicationClient.getTags(versionHash);
		int i = 0;
		for (Object object : jsonArray) {
			JSONObject jsonObject = (JSONObject)object;
			if(properties.containsKey(jsonObject.getString(PARAM_NAME_KEY))){
				i++;
				Assert.assertEquals("Value of the property doesn't match.", properties.get(jsonObject.getString(PARAM_NAME_KEY)),
				                    jsonObject.getString(PARAM_NAME_VALUE));
			}
		}
		Assert.assertTrue("One or more Properties are not added.", i == properties.size());
	}

	@SetEnvironment(executionEnvironments = { ExecutionEnvironment.PLATFORM})
	@Test(description = "Testing update tags", dependsOnMethods = {"testAddTags"})
	public void testUpdateTags() throws Exception {
		String versionHash = applicationClient.getVersionHash(applicationName, applicationRevision);
		JSONArray jsonArray = applicationClient.getTags(versionHash);
		JSONObject jsonObject = (JSONObject)jsonArray.get(0);
		String prevKey = jsonObject.getString(PARAM_NAME_KEY);
		String newKey = RandomStringUtils.random(5, true, false);
		String newValue = RandomStringUtils.random(6, true, false);
		applicationClient.updateTag(versionHash, prevKey, newKey, newValue);
		JSONArray updatedJSONArray = applicationClient.getTags(versionHash);
		boolean containsNewKey = false;
		for (Object object : updatedJSONArray) {
			JSONObject jsonOBJ = (JSONObject)object;
			if(newKey.equals(jsonOBJ.getString(PARAM_NAME_KEY))){
				containsNewKey = true;
				Assert.assertEquals("Value of the property doesn't match.", newValue, jsonOBJ.getString(PARAM_NAME_VALUE));
			}
		}
		Assert.assertTrue("Property is not updated.", containsNewKey);
	}

	@SetEnvironment(executionEnvironments = { ExecutionEnvironment.PLATFORM})
	@Test(description = "Testing update tags", dependsOnMethods = {"testUpdateTags"})
	public void testDeleteTags() throws Exception {
		String versionHash = applicationClient.getVersionHash(applicationName, applicationRevision);
		JSONArray jsonArray = applicationClient.getTags(versionHash);
		JSONObject jsonObject = (JSONObject)jsonArray.get(0);
		String key = jsonObject.getString(PARAM_NAME_KEY);
		applicationClient.deleteTag(versionHash, key);
		JSONArray updatedJSONArray = applicationClient.getTags(versionHash);
		boolean containsKey = false;
		for (Object object : updatedJSONArray) {
			JSONObject jsonOBJ = (JSONObject)object;
			if(key.equals(jsonOBJ.getString(PARAM_NAME_KEY))){
				containsKey = true;
			}
		}
		Assert.assertNotEquals("Property is not deleted.", containsKey);
	}

	@SetEnvironment(executionEnvironments = { ExecutionEnvironment.PLATFORM})
	@Test(description = "Testing create version", dependsOnMethods = {"testDeleteTags"})
	public void testCreateVersion() throws Exception {
		String applicationRevision =
				AppCloudIntegrationTestUtils.getPropertyValue(AppCloudIntegrationTestConstants.APP_NEW_REVISION_KEY);
		File uploadArtifact = new File(TestConfigurationProvider.getResourceLocation() + fileName);
        applicationClient.createNewApplication(applicationName, this.runtimeID, applicationType, applicationRevision,
                                               applicationDescription, this.fileName, properties, tags, uploadArtifact,
                                               true, containerSpecMemory, containerSpecCpu);

		//Wait until creation finished
		RetryApplicationActions(applicationRevision, AppCloudIntegrationTestConstants.STATUS_RUNNING, "Application version creation");
	}


	@SetEnvironment(executionEnvironments = { ExecutionEnvironment.PLATFORM})
	@Test(description = "Testing get logs", dependsOnMethods = {"testCreateVersion"})
	public void testGetLogs() throws Exception {
		long timeOutPeriod = AppCloudIntegrationTestUtils.getTimeOutPeriod();
		Thread.sleep(timeOutPeriod);
		String applicationHash = applicationClient.getApplicationHash(applicationName);
		String result = logsClient.getSnapshotLogs(applicationHash, applicationRevision);
		assertLogContent(result);
	}

	@SetEnvironment(executionEnvironments = { ExecutionEnvironment.PLATFORM})
	@Test(description = "Testing delete version", dependsOnMethods = {"testGetLogs"})
	public void testDeleteVersion() throws Exception {
		String versionHash = applicationClient.getVersionHash(applicationName, applicationRevision);
		applicationClient.deleteVersion(versionHash);
		JSONArray jsonArray = applicationClient.getVersions(applicationName);
		boolean isDeleted = true;
		for (Object obj : jsonArray) {
			String versionName = obj.toString();
			if(versionName.equals(applicationRevision)){
				isDeleted = false;
			}
		}
		Assert.assertTrue("Version Deletion Failed", isDeleted);
	}


	@AfterClass(alwaysRun = true)
	public void cleanEnvironment() throws Exception {
		String applicationHash = applicationClient.getApplicationHash(applicationName);
		boolean isDeleted = applicationClient.deleteApplication(applicationHash);
		Assert.assertEquals("Application deletion failed", isDeleted, true);
		long timeOutPeriod = AppCloudIntegrationTestUtils.getTimeOutPeriod();
                Thread.sleep(timeOutPeriod);
	}

	/**
	 * Retry for application status to be changed to expected value for configured no of retries
	 * @param applicationRevision Revision of the application wanted to check status for
	 * @param expectedStatus Expected Status of the application
	 * @param action Action to log in error messages
	 * @throws java.lang.Exception
	 */
	private void RetryApplicationActions(String applicationRevision, String expectedStatus, String action)
			throws Exception {
		long timeOutPeriod = AppCloudIntegrationTestUtils.getTimeOutPeriod();
		int retryCount = AppCloudIntegrationTestUtils.getTimeOutRetryCount();
		int round = 1;
		String actualStatus = null;
		while (round <= retryCount) {
			JSONObject result = applicationClient.getApplicationBean(applicationName);
			actualStatus = ((JSONObject) ((JSONObject) result
					.get(AppCloudIntegrationTestConstants.PROPERTY_VERSIONS_NAME))
					.get(applicationRevision)).getString(AppCloudIntegrationTestConstants.PROPERTY_STATUS_NAME);
			log.info("Application current status is : " + actualStatus);
			if(!expectedStatus.equals(actualStatus)){
				Thread.sleep(timeOutPeriod);
				round++;
				continue;
			}
			break;
		}
		Assert.assertEquals(action + " failed", expectedStatus, actualStatus);
	}

	protected abstract void assertLogContent(String logContent);
}
