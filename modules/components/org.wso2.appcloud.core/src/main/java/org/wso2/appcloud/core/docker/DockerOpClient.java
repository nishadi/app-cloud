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

import io.fabric8.docker.client.Config;
import io.fabric8.docker.client.ConfigBuilder;
import io.fabric8.docker.client.DefaultDockerClient;
import io.fabric8.docker.client.DockerClient;
import io.fabric8.docker.dsl.EventListener;
import io.fabric8.docker.dsl.OutputHandle;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class DockerOpClient {

    private static Log log = LogFactory.getLog(DockerOpClient.class);

    DockerClient dockerClient;
    OutputHandle handle;

    final CountDownLatch buildDone = new CountDownLatch(1);
    final CountDownLatch pushDone = new CountDownLatch(1);

    public DockerOpClient() {
        Config config = new ConfigBuilder()
                .withMasterUrl(DockerOpClientConstants.DEFAULT_DOCKER_URL)
                .build();
        dockerClient = new DefaultDockerClient(config);
    }

    public DockerOpClient(String uri) {
        Config config = new ConfigBuilder()
                .withMasterUrl(uri)
                .build();
        dockerClient = new DefaultDockerClient(config);
    }

    public void createDockerFile(String appType, String artifactName, String dockerFilePath) throws IOException {
        String dockerRegistryUrl = DockerUtil.getDockerRegistryUrl();
        String dockerBaseImageName = DockerUtil.getBaseImageName(appType);
        String dockerBaseImageVersion = DockerUtil.getBaseImageVersion(appType);
        String baseImangeConfig = DockerOpClientConstants.DOCKER_COMMAND_FROM + " " + dockerRegistryUrl + "/" +
                dockerBaseImageName + ":" + dockerBaseImageVersion + "\r\n";
        List<String> dockerFileConfigs = new ArrayList<String>();
        dockerFileConfigs.add(baseImangeConfig);
        String deploymentLocation = DockerUtil.getDeploymentLocation(appType);
        String artifactCopyConfig = DockerOpClientConstants.DOCKER_COMMAND_COPY + " " + artifactName + " " +
                deploymentLocation + "\r\n";
        dockerFileConfigs.add(artifactCopyConfig);
        FileUtils.writeLines(new File(dockerFilePath), dockerFileConfigs);
    }

    public void buildDockerImage(String repoUrl, String imageName, String tag, String dockerFileUrl) throws
            InterruptedException, IOException {

        String dockerImage = repoUrl + "/" + imageName + ":" + tag;

        handle = dockerClient.image().build()
                .withRepositoryName(dockerImage)
                .usingListener(new EventListener() {
                    @Override
                    public void onSuccess(String message) {
                        log.info("Success:" + message);
                        buildDone.countDown();
                    }

                    @Override
                    public void onError(String messsage) {
                        log.error("Failure:" +messsage);
                        buildDone.countDown();
                    }

                    @Override
                    public void onEvent(String event) {
                        log.info(event);
                    }
                })
                .fromFolder(dockerFileUrl);
        buildDone.await();
        handle.close();
    }

    public void pushDockerImage(String repoUrl, String imageName, String tag) throws InterruptedException, IOException {
        String dockerImgeName = repoUrl + "/" + imageName;

        handle = dockerClient.image().withName(dockerImgeName).push().usingListener(new EventListener() {
            @Override
            public void onSuccess(String message) {
                log.info("Success:" + message);
                pushDone.countDown();
            }

            @Override
            public void onError(String message) {
                log.error("Error:" + message);
                pushDone.countDown();
            }

            @Override
            public void onEvent(String event) {
                log.info(event);
            }
        }).toRegistry();

        pushDone.await();
        handle.close();
    }

    public void clientClose() throws IOException {
        dockerClient.close();
    }
}
