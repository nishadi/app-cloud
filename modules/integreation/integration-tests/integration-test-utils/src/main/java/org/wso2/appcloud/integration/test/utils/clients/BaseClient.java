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
import org.apache.commons.lang.CharEncoding;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.wso2.appcloud.integration.test.utils.AppCloudIntegrationTestConstants;
import org.wso2.appcloud.integration.test.utils.AppCloudIntegrationTestException;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseClient {

	private static final Log log = LogFactory.getLog(BaseClient.class);

    protected static final String HEADER_SET_COOKIE = "Set-Cookie";
	protected static final String HEADER_COOKIE = "Cookie";
	protected static final String HEADER_CONTENT_TYPE = "Content-Type";
	protected static final String MEDIA_TYPE_X_WWW_FORM = "application/x-www-form-urlencoded";
	protected static final String PARAM_NAME_ACTION = "action";
	protected static final String ACTION_NAME_LOGIN = "login";
	protected static final String PARAM_EQUALIZER = "=";
	protected static final String PARAM_SEPARATOR = "&";
	protected static final String PARAM_NAME_USER_NAME = "userName";
	protected static final String PARAM_NAME_PASSWORD = "password";

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


	protected void setHTTPHeader(String headerName, String value) {
		requestHeaders.put(headerName, value);
	}

	protected String getHTTPHeader(String headerName) {
		return requestHeaders.get(headerName);
	}

	protected void removeHTTPHeader(String headerName) {
		requestHeaders.remove(headerName);
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

        login(username, password);
    }

    protected void checkErrors(HttpResponse response) throws AppCloudIntegrationTestException {
        JSONObject jsonObject = new JSONObject(response.getData());
        if (jsonObject.keySet().contains("error")) {
            throw new AppCloudIntegrationTestException("Operation not successful: "
                                                       + jsonObject.get(AppCloudIntegrationTestConstants.RESPONSE_MESSAGE_NAME).toString());
        }
    }

	/**
	 * login to app mgt
	 *
	 * @param userName username
	 * @param password password
	 * @throws Exception
	 */
	protected void login(String userName, String password) throws Exception {
		HttpResponse response = HttpRequestUtil.doPost(
				new URL(getBackEndUrl() + AppCloudIntegrationTestConstants.APPMGT_URL_SURFIX
				+ AppCloudIntegrationTestConstants.APPMGT_USER_LOGIN),
				PARAM_NAME_ACTION + PARAM_EQUALIZER + ACTION_NAME_LOGIN + PARAM_SEPARATOR + PARAM_NAME_USER_NAME
				+ PARAM_EQUALIZER + userName + PARAM_SEPARATOR + PARAM_NAME_PASSWORD + PARAM_EQUALIZER
				+ password, getRequestHeaders());

		if (response.getResponseCode() == HttpStatus.SC_OK && response.getData().equals("true")) {
			String session = getSession(response.getHeaders());
			if (session == null) {
				throw new AppCloudIntegrationTestException("No session cookie found with response");
			}
			setSession(session);
		} else {
			throw new AppCloudIntegrationTestException("Login failed " + response.getData());
		}
	}

	/**
	 * Do post request to appfactory.
	 *
	 * @param urlSuffix url suffix from the block layer
	 * @param keyVal  post body
	 * @return httpResponse
	 */
	public HttpResponse doPostRequest(String urlSuffix, Map<String, String> keyVal) throws AppCloudIntegrationTestException {
		String postBody = generateMsgBody(keyVal);
		try {
			return HttpRequestUtil.doPost(new URL(getBackEndUrl() + AppCloudIntegrationTestConstants.APPMGT_URL_SURFIX
			                                      + urlSuffix), postBody,
			                              getRequestHeaders());
		} catch (Exception e) {
			final String msg = "Error occurred while doing a post :";
			log.error(msg, e);
			throw new AppCloudIntegrationTestException(msg, e);
		}

	}

	/**
	 * Returns a String that is suitable for use as an application/x-www-form-urlencoded list of parameters in an
	 * HTTP PUT or HTTP POST.
	 *
	 * @param keyVal parameter map
	 * @return message body
	 */
	public String generateMsgBody(Map<String, String> keyVal) {
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		for (Map.Entry<String, String> keyValEntry : keyVal.entrySet()) {
			qparams.add(new BasicNameValuePair(keyValEntry.getKey(), keyValEntry.getValue()));
		}
		return URLEncodedUtils.format(qparams, CharEncoding.UTF_8);
	}
}
