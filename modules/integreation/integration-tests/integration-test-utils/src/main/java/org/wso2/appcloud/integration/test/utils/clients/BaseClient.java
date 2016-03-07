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

package org.wso2.appcloud.integration.test.utils.clients;

import org.apache.commons.httpclient.HttpStatus;
import org.json.JSONObject;
import org.wso2.appcloud.integration.test.utils.AppCloudIntegrationTestException;
import org.wso2.appcloud.integration.test.utils.AppCloudIntegrationTestUtils;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.carbon.integration.common.admin.client.AuthenticatorClient;
import org.wso2.carbon.integration.common.utils.LoginLogoutClient;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLStreamException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class BaseClient {

    public static final String HEADER_SET_COOKIE = "Set-Cookie";
    public static final String HEADER_COOKIE = "Cookie";
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String MEDIA_TYPE_X_WWW_FORM = "application/x-www-form-urlencoded";

    private String backEndUrl;
    private Map<String, String> requestHeaders = new HashMap<String, String>();
    private static AutomationContext context;

    protected String getBackEndUrl() {
        return backEndUrl;
    }
    protected void setBackEndUrl(String backEndUrl) {
        this.backEndUrl = backEndUrl;
    }
    protected Map<String, String> getRequestHeaders() {
        return requestHeaders;
    }

    /**
     * Get session
     *
     * @param responseHeaders response headers
     * @return session
     */
    protected String getSession(Map<String, String> responseHeaders) {
        return responseHeaders.get(HEADER_SET_COOKIE);
    }

    /**
     * Set session
     *
     * @param session session
     */
    protected void setSession(String session) {
        requestHeaders.put(HEADER_COOKIE, session);
    }

    /**
     * Construct authenticates REST client to invoke appmgt functions
     *
     * @param backEndUrl backend url
     * @param username   username
     * @param password   password
     * @throws Exception
     */
    public BaseClient(String backEndUrl, String username, String password) throws Exception {

        setBackEndUrl(backEndUrl);
        if (getRequestHeaders().get(HEADER_CONTENT_TYPE) == null) {
            getRequestHeaders().put(HEADER_CONTENT_TYPE, MEDIA_TYPE_X_WWW_FORM);
        }

        login(context = AppCloudIntegrationTestUtils.getAutomationContext());
    }

    private String login(AutomationContext context)
            throws IOException, XPathExpressionException, URISyntaxException, SAXException, XMLStreamException,
            LoginAuthenticationExceptionException {
        LoginLogoutClient loginLogoutClient = new LoginLogoutClient(context);
        return loginLogoutClient.login();
    }

    protected void checkErrors(HttpResponse response) throws AppCloudIntegrationTestException {
        JSONObject jsonObject = new JSONObject(response.getData());
        if (jsonObject.keySet().contains("error")) {
            throw new AppCloudIntegrationTestException("Operation not successful: " + jsonObject.get("message").toString());
        }
    }
}
