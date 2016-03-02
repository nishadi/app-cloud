/*
* Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package org.wso2.appcloud.provisioning.runtime.Utils;

import io.fabric8.kubernetes.api.KubernetesHelper;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.appcloud.common.util.AppCloudUtil;
import org.wso2.appcloud.provisioning.runtime.KubernetesPovisioningConstants;
import org.wso2.appcloud.provisioning.runtime.beans.ApplicationContext;
import org.wso2.appcloud.provisioning.runtime.beans.TenantInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * This will have the utility methods to provision kubernetes
 */

public class KubernetesProvisioningUtils {
    private static final Log log = LogFactory.getLog(KubernetesProvisioningUtils.class);

    /**
     * This method will create a common Kubernetes client object with authentication to the Kubernetes master server
     *
     * @return Kubernetes client object
     */
    public static KubernetesClient getFabric8KubernetesClient() {

        KubernetesClient kubernetesClient = null;

        Config config = new Config();
        config.setUsername(AppCloudUtil.getPropertyValue(KubernetesPovisioningConstants.PROPERTY_MASTER_USERNAME));
        config.setPassword(AppCloudUtil.getPropertyValue(KubernetesPovisioningConstants.PROPERTY_MASTER_PASSWORD));
        config.setNoProxy(
                new String[] { AppCloudUtil.getPropertyValue(KubernetesPovisioningConstants.PROPERTY_KUB_MASTER_URL) });
        config.setMasterUrl(AppCloudUtil.getPropertyValue(KubernetesPovisioningConstants.PROPERTY_KUB_MASTER_URL));
        config.setApiVersion(AppCloudUtil.getPropertyValue(KubernetesPovisioningConstants.PROPERTY_KUB_API_VERSION));
        kubernetesClient = new DefaultKubernetesClient(config);

        return kubernetesClient;
    }

    /**
     * This utility method will generate the namespace of the current application context
     *
     * @param applicationContext context of the current application
     * @return namespace of the current application context
     */
    public static Namespace getNameSpace(ApplicationContext applicationContext) {

        // todo: consider constraints of 24 character limit in namespace.
        String ns = applicationContext.getTenantInfo().getTenantDomain();
        ns = ns.replace(".", "-").toLowerCase();
        ObjectMeta metadata = new ObjectMetaBuilder()
                .withName(ns)
                .build();
        return new NamespaceBuilder()
                .withMetadata(metadata)
                .build();
    }

    /**
     * This utility method will provide the list of pods for particular application
     *
     * @param applicationContext context of the current application
     * @return list of pods related for the current application context
     */
    public static PodList getPods (ApplicationContext applicationContext){

        Map<String, String> selector = getLableMap(applicationContext);
        KubernetesClient kubernetesClient = getFabric8KubernetesClient();
        PodList podList = kubernetesClient.inNamespace(getNameSpace(applicationContext).getMetadata()
                .getName()).pods().withLabels(selector).list();
        return podList;
    }

    /**
     * This utility method will provide the list of services for particular application
     *
     * @param applicationContext context of the current application
     * @return list of services related for the current application context
     */
    public static ServiceList getServices(ApplicationContext applicationContext){
        Map<String, String> selector = getLableMap(applicationContext);
        KubernetesClient kubernetesClient = getFabric8KubernetesClient();
        ServiceList serviceList = kubernetesClient.inNamespace(getNameSpace(applicationContext).getMetadata()
                .getName()).services().withLabels(selector).list();
        return serviceList;
    }
    /**
     * This utility method will generate the appropriate selector for filter out the necessary kinds for particular
     * application
     *
     * @param applicationContext context of the current application
     * @return selector which can be use to filter out certain kinds
     */
    public static Map<String, String> getLableMap(ApplicationContext applicationContext) {

        //todo generate a common selector valid for all types of application
        Map<String, String> selector = new HashMap<>();
        selector.put("app", applicationContext.getId());
        selector.put("version", applicationContext.getVersion());
        return selector;
    }

    /**
     * Generate a unique name for an ingress
     * @param applicationContext
     * @param domain
     * @param serviceName
     * @return generated unique name for ingress (appName-appVersion-service)
     */
    public static String createIngressMetaName(ApplicationContext applicationContext, String domain, String serviceName){

        return (domain)
                .replace(".","-").toLowerCase();
    }

    public static ApplicationContext getApplicationContext(String id, String version, String type, int tenantId,
            String tenantDomain) {

        ApplicationContext applicationContext = new ApplicationContext();
        applicationContext.setId(id);
        applicationContext.setVersion(version);
        applicationContext.setType(type);
        TenantInfo tenantInfo = new TenantInfo();
        tenantInfo.setTenantId(tenantId);
        tenantInfo.setTenantDomain(tenantDomain);
        applicationContext.setTenantInfo(tenantInfo);
        return applicationContext;
    }

    /**
     * This util will generate the deployment path for application
     *
     * @param applicationContext application context
     * @return
     */
    public static String getDeploymentPath(ApplicationContext applicationContext){

        String tenantDomain = applicationContext.getTenantInfo().getTenantDomain();
        String appId = applicationContext.getId();
        String appVersion = applicationContext.getVersion();

        return "/" + tenantDomain + "/webapps/" + appId + "-" + appVersion;
    }

    /**
     * This utility method will return the pod status an application
     *
     * @param applicationContext application context object
     * @return
     */
    public static boolean getPodStatus(ApplicationContext applicationContext){
        PodList podList = getPods(applicationContext);

        if (podList.getItems().size() == 0) {
            return false;
        } else {
            for (Pod pod : podList.getItems()) {
                String status = KubernetesHelper.getPodStatusText(pod);
                if (!"Running".equals(status)) {
                    return false;
                }
            }
        }
        return true;

    }
}
