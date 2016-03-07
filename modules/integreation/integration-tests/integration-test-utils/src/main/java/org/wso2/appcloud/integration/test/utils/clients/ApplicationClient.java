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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.wso2.appcloud.integration.test.utils.AppCloudIntegrationTestException;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;

import java.net.URL;

public class ApplicationClient extends BaseClient{
    private static final Log log = LogFactory.getLog(ApplicationClient.class);

    /**
     * Construct authenticates REST client to invoke appmgt functions
     *
     * @param backEndUrl backend url
     * @param username   username
     * @param password   password
     * @throws Exception
     */
    public ApplicationClient(String backEndUrl, String username, String password) throws Exception {
        super(backEndUrl, username, password);
    }

    public void createNewApplication(String applicationName, String runtime, String appTypeName,
            String applicationRevision, String applicationDescription, String uploadedFileName,
            String runtimeProperties, String tags) throws Exception {
        HttpResponse response = HttpRequestUtil
                .doPost(new URL("https://localhost:9443/" + "appmgt/site/blocks/" + "application/application.jag"),
                        "action=createApplication&applicationName=" + applicationName + "&applicationDescription=" +
                                applicationDescription + "&runtime=" + runtime + "&appTypeName=" + appTypeName +
                                "&applicationRevision= " + applicationRevision +
                                "&uploadedFileName=" + uploadedFileName + "&applicationDescription="
                                + applicationDescription + "&runtimeProperties=" + runtimeProperties + "" + "&tags="
                                + tags, getRequestHeaders());

        if (response.getResponseCode() == HttpStatus.SC_OK) {
            checkErrors(response);
            JSONObject jsonObject = new JSONObject(response.getData());
/*            if (!jsonObject.getString("message").equals(
                    "Application was created under Repository type git")) {
                throw new AppCloudIntegrationTestException("CreateNewApplication failed : " + response.getData());
            }*/
            log.info("App creation response:" + jsonObject.getString("message"));
        } else {
            throw new AppCloudIntegrationTestException("CreateNewApplication failed " + response.getData());
        }
    }

}
