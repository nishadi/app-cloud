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
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.HttpClientBuilder;
import org.wso2.appcloud.integration.test.utils.AppCloudIntegrationTestConstants;

import java.io.File;

public class ApplicationClient extends BaseClient{
	private static final Log log = LogFactory.getLog(ApplicationClient.class);
	protected static final String CREATE_APPLICATION_ACTION = "createApplication";
	protected static final String PARAM_NAME_APPLICATION_NAME = "applicationName";
	protected static final String PARAM_NAME_APPLICATION_DESCRIPTION = "applicationDescription";
	protected static final String PARAM_NAME_RUNTIME = "runtime";
	protected static final String PARAM_NAME_APP_TYPE_NAME = "appTypeName";
	protected static final String PARAM_NAME_APPLICATION_REVISION = "applicationRevision";
	protected static final String PARAM_NAME_UPLOADED_FILE_NAME = "uploadedFileName";
	protected static final String PARAM_NAME_PROPERTIES = "runtimeProperties";
	protected static final String PARAM_NAME_TAGS = "tags";
	public static final String PARAM_NAME_IS_FILE_ATTACHED = "isFileAttached";
	public static final String PARAM_NAME_FILEUPLOAD = "fileupload";

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
            String applicationVersion, String applicationDescription, String uploadedFileName,
            String runtimeProperties, String tags, File uploadArtifact) throws Exception {
	    String endpoint = getBackEndUrl() + AppCloudIntegrationTestConstants.APPMGT_URL_SURFIX
	                      + AppCloudIntegrationTestConstants.REST_APPLICATION_ENDPOINT;


	    HttpClient httpclient = HttpClientBuilder.create().build();;
	    HttpPost httppost = new HttpPost(endpoint);

	    MultipartEntityBuilder builder = MultipartEntityBuilder.create();
	    builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
	    builder.addPart(PARAM_NAME_FILEUPLOAD, new FileBody(uploadArtifact));
	    builder.addPart(PARAM_NAME_ACTION, new StringBody(CREATE_APPLICATION_ACTION, ContentType.TEXT_PLAIN));
	    builder.addPart(PARAM_NAME_APPLICATION_NAME, new StringBody(applicationName, ContentType.TEXT_PLAIN));
	    builder.addPart(PARAM_NAME_APPLICATION_DESCRIPTION, new StringBody(applicationDescription, ContentType.TEXT_PLAIN));
	    builder.addPart(PARAM_NAME_RUNTIME, new StringBody(runtime, ContentType.TEXT_PLAIN));
	    builder.addPart(PARAM_NAME_APP_TYPE_NAME, new StringBody(appTypeName, ContentType.TEXT_PLAIN));
	    builder.addPart(PARAM_NAME_APPLICATION_REVISION, new StringBody(applicationVersion, ContentType.TEXT_PLAIN));
	    builder.addPart(PARAM_NAME_UPLOADED_FILE_NAME, new StringBody(uploadedFileName, ContentType.TEXT_PLAIN));
	    builder.addPart(PARAM_NAME_PROPERTIES, new StringBody(runtimeProperties, ContentType.TEXT_PLAIN));
	    builder.addPart(PARAM_NAME_TAGS, new StringBody(tags, ContentType.TEXT_PLAIN));
	    builder.addPart(PARAM_NAME_IS_FILE_ATTACHED, new StringBody(Boolean.TRUE.toString(), ContentType.TEXT_PLAIN));//Setting true to send the file in request

	    httppost.setEntity(builder.build());

	    httppost.setHeader(HEADER_COOKIE, getRequestHeaders().get(HEADER_COOKIE));

	    org.apache.http.HttpResponse response = httpclient.execute(httppost);

        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
//            checkErrors(response);
//            JSONObject jsonObject = new JSONObject(response.getData());
//            log.info("App creation response : " + jsonObject.getString(AppCloudIntegrationTestConstants.RESPONSE_MESSAGE_NAME));
        } else {
//            throw new AppCloudIntegrationTestException("CreateNewApplication failed " + response.getData());
        }
    }

}
