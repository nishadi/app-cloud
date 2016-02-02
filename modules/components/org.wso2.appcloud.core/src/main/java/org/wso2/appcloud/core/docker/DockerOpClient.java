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

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.SearchItem;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.command.BuildImageResultCallback;
import com.github.dockerjava.core.command.PushImageResultCallback;
import com.github.dockerjava.jaxrs.DockerCmdExecFactoryImpl;

import java.io.File;
import java.util.List;

public class DockerOpClient {

    DockerClient dockerClient;

    DockerOpClient() {
        dockerClient = DockerClientBuilder.getInstance(DockerOpClientConstants.DEFAULT_DOCKER_URL).build();
    }

    DockerOpClient(String uri) {
        DockerClientConfig config = DockerClientConfig.createDefaultConfigBuilder()
                .withVersion(DockerOpClientConstants.DOCKER_CLIENT_API_VERSION)
                .withUri(uri)
                .build();

        DockerCmdExecFactoryImpl dockerCmdExecFactory = new DockerCmdExecFactoryImpl()
                .withReadTimeout(DockerOpClientConstants.DOCKER_CLIENT_READ_TIMEOUT)
                .withConnectTimeout(DockerOpClientConstants.DOCKER_CLIENT_CONNECTION_TIMEOUT)
                .withMaxTotalConnections(DockerOpClientConstants.DOCKER_CLIENT_TOTAL_CONNECTIONS)
                .withMaxPerRouteConnections(DockerOpClientConstants.DOCKER_CLIENT_MAX_PER_ROUTE_CONNECTIONS);

        dockerClient = DockerClientBuilder.getInstance(config)
                .withDockerCmdExecFactory(dockerCmdExecFactory)
                .build();
    }

    public String buildDockerImage(String repoUrl, String imageName, String tag, String dockerFileUrl) {
        File baseDir = new File(dockerFileUrl);
        String dockerImageTag = repoUrl + "/" + imageName + ":" + tag;
        return dockerClient.buildImageCmd(baseDir).withTag(dockerImageTag).exec(new BuildImageResultCallback())
                .awaitImageId();
    }

    public void pushDockerImage(String repoUrl, String imageName, String tag) {
        String dockerImge = repoUrl + "/" + imageName;
        dockerClient.pushImageCmd(dockerImge).withTag(tag).exec(new PushImageResultCallback()).awaitSuccess();
    }

    public void deleteDockerImagewithForce(String imageId) {
        dockerClient.removeImageCmd(imageId).withForce().exec();
    }

    public void deleteDockerImage(String imageId) {
        dockerClient.removeImageCmd(imageId).exec();
    }

    public List<SearchItem> searchImage(String term) {
        return dockerClient.searchImagesCmd(term).exec();
    }
}
