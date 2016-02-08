package org.wso2.appcloud.common;/*
 *
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

/**
 * Constant for AppCloudUtil
 */

public class AppCloudConstant {

    public static final String CONFIG_FOLDER = "appcloud";
    public static final String CONFIG_FILE_NAME = "appcloud.properties";
    public static final String SIGNED_JWT_AUTH_USERNAME = "Username";


    public enum events {
        ARTIFACT_UPLOAD, DOCKER_FILE_CREATE, DOCKER_IMAGE_BUILD, DOCKER_REGISTRY_PUSH, KUBE_DEPLOY
    }

    public enum eventStatus {
        EVENT_SUCCEEDED, EVENT_PENDING, EVENT_FAILED
    }


}
