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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.integration.common.admin.client.AuthenticatorClient;
import org.wso2.carbon.integration.common.utils.LoginLogoutClient;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLStreamException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.rmi.RemoteException;

public class AppCloudIntegrationTests {

    private static final Log log = LogFactory.getLog(AppCloudIntegrationTests.class);

    protected String defaultAdmin;
    protected String defaultAdminPassword;
    protected String defaultAppName;
    protected String serverUrl;
    protected String tenantDomain;
    private String superTenantSession;
    private static AutomationContext context;

    public AppCloudIntegrationTests() {

        defaultAdmin = AppCloudIntegrationTestUtils.getAdminUsername();
        defaultAdminPassword = AppCloudIntegrationTestUtils.getAdminPassword();
        defaultAppName = AppCloudIntegrationTestUtils.getPropertyValue(AppCloudIntegrationTestConstants.DEFAULT_APP_APP_NAME);
        serverUrl = AppCloudIntegrationTestUtils.getPropertyValue(AppCloudIntegrationTestConstants.URLS_APPCLOUD);
        tenantDomain =  AppCloudIntegrationTestUtils.getPropertyValue(AppCloudIntegrationTestConstants.DEFAULT_TENANT_TENANT_DOMAIN);
    }

    /**
     * Start test execution with super tenant login
     *
     * @throws java.lang.Exception
     */
    private void init() throws Exception {
        superTenantSession = login(context = AppCloudIntegrationTestUtils.getAutomationContext());
    }

    /**
     * Clean up the changes
     */
    protected void cleanup() {
        log.info("cleanup called");
    }

    /**
     * Login as any user
     *
     * @param backendUrl backend url
     * @param username   username
     * @param password   password
     * @param host       host
     * @return session
     * @throws RemoteException
     * @throws LoginAuthenticationExceptionException
     */
    protected String login(String backendUrl, String username, String password, String host)
            throws RemoteException, LoginAuthenticationExceptionException {
        AuthenticatorClient client = new AuthenticatorClient(backendUrl + "services/");
        return client.login(username, password, host);
    }

    private String login(AutomationContext context) throws IOException, XPathExpressionException, URISyntaxException,
            SAXException, XMLStreamException, LoginAuthenticationExceptionException {
        LoginLogoutClient loginLogoutClient = new LoginLogoutClient(context);
        return loginLogoutClient.login();
    }
}
