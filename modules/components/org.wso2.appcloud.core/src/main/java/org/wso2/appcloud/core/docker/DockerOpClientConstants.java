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

public class DockerOpClientConstants {

    public static final String DOCKER_CLIENT_API_VERSION = "1.19";
    public static final int DOCKER_CLIENT_READ_TIMEOUT = 1000;
    public static final int DOCKER_CLIENT_TOTAL_CONNECTIONS = 1000;
    public static final int DOCKER_CLIENT_MAX_PER_ROUTE_CONNECTIONS = 10;
    public static final int DOCKER_CLIENT_CONNECTION_TIMEOUT = 1000;
    public static final String DEFAULT_DOCKER_URL = "http://localhost:2375";
}
