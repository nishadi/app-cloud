/*
 *  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.wso2.appcloud.core.docker;

public class DockerUtil {

    public static String getDockerRegistryUrl() {
        return DockerOpClientConstants.DEFAULT_DOCKER_REG_URL;
    }

    public static String getBaseImageName(String appType) {
        String baseImageName;
        switch (appType) {
            case "war":
                baseImageName = DockerOpClientConstants.DOCKER_BASE_IMAGE_TOMCAT;
                break;
            case "car":
                baseImageName = DockerOpClientConstants.DOCKER_BASE_IMAGE_WSO2AS;
                break;
            case "msf4j":
                baseImageName = DockerOpClientConstants.DOCKER_BASE_IMAGE_MSF4J;
                break;
            default:
                baseImageName = DockerOpClientConstants.DOCKER_BASE_IMAGE_UBUNTU;
                break;
        }
        return baseImageName;
    }

    public static String getBaseImageVersion(String appType){
        String baseImageVersion;
        switch (appType) {
            case "war":
                baseImageVersion = DockerOpClientConstants.DOCKER_TOMCAT_VERSION;
                break;
            case "car":
                baseImageVersion = DockerOpClientConstants.DOCKER_MSF4J_VERSION;
                break;
            default:
                baseImageVersion = DockerOpClientConstants.DOCKER_UBUNTU_VERSION;
                break;
        }

        return baseImageVersion;
    }

    public static String getDeploymentLocation(String appType) {
        return DockerOpClientConstants.DOCKER_WAR_LOCATION;
    }
}
