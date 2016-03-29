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

package org.wso2.appcloud.integration.test.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;

import javax.xml.xpath.XPathExpressionException;
import java.util.HashMap;
import java.util.Map;

public class AppCloudIntegrationTestUtils {

    private static final Log log = LogFactory.getLog(AppCloudIntegrationTestUtils.class);

    private static AutomationContext context;
    private static String tenantDomain;



    static {
        try {
            context = new AutomationContext(AppCloudIntegrationTestConstants.APPCLOUD_PRODUCT_GROUP, TestUserMode.SUPER_TENANT_ADMIN);
        } catch (XPathExpressionException e) {
            log.error("Error occurred while initializing automation context",e);
        }
    }

    public static AutomationContext getAutomationContext(){
        return context;
    }

    /**
     * Get value passing xpath
     *
     * @param xPath expression
     * @return value
     * @throws XPathExpressionException
     */
    public static String getPropertyValue(String xPath) throws IllegalArgumentException {
        try {
            return context.getConfigurationValue(xPath);
        } catch (XPathExpressionException e) {
            throw new IllegalArgumentException("Error reading " + xPath, e);
        }
    }

	public static NodeList getPropertyNodes(String xPath) throws IllegalArgumentException{
		try {
			return context.getConfigurationNodeList(xPath);
		} catch (XPathExpressionException e) {
			throw new IllegalArgumentException("Error reading " + xPath, e);
		}
	}

    public static String getAdminUsername() {
        String tenantDomain = getDefaultTenantDomain();
        return getPropertyValue(AppCloudIntegrationTestConstants.DEFAULT_TENANT_ADMIN) + "@" + tenantDomain;
    }

    public static String getDefaultTenantDomain() {
        tenantDomain = getPropertyValue(AppCloudIntegrationTestConstants.DEFAULT_TENANT_TENANT_DOMAIN);
        return tenantDomain;
    }

    public static String getAdminPassword() {
        return getPropertyValue(AppCloudIntegrationTestConstants.DEFAULT_TENANT_ADMIN_PASSWORD);
    }

    public static long getTimeOutPeriod() {
        String timeOutPeriod = getPropertyValue(AppCloudIntegrationTestConstants.TIMEOUT_PERIOD);
        long result = 10000L;
        try {
            result = Long.parseLong(timeOutPeriod);
        } catch (NumberFormatException e) {
            // NOP
        }
        return result;
    }

    public static int getTimeOutRetryCount() {
        String retryCount = getPropertyValue(AppCloudIntegrationTestConstants.TIMEOUT_RETRY_COUNT);
        int result = 8;
        try {
            result = Integer.parseInt(retryCount);
        } catch (NumberFormatException e) {
            // NOP
        }
        return result;
    }

	/**
	 * [{key="key1",value=value1},...]
	 * @param propertyNodes
	 * @return
	 */
	public static String getKeyValuePairAsJsonFromConfig(NodeList propertyNodes) {
		StringBuilder result = new StringBuilder("[");
		for (int i = 0 ; i < propertyNodes.getLength() ; i++) {
			if(i != 0){
				result.append(",");
			}
			Element element = (Element)propertyNodes.item(i);
			String key = element.getAttribute(AppCloudIntegrationTestConstants.ATTRIBUTE_KEY);
			String value = element.getTextContent();
			result.append("{\"key\":\"");
			result.append(key);
			result.append("\",\"value\":\"");
			result.append(value);
			result.append("\"}");
		}
		result.append("]");
		return result.toString();
	}

	/**
	 * [{key="key1",value=value1},...]
	 * @param propertyNodes
	 * @return
	 */
	public static Map<String, String> getKeyValuePairsFromConfig(NodeList propertyNodes) {
		Map<String, String> keyValuePair = new HashMap<String, String>();
		for (int i = 0 ; i < propertyNodes.getLength() ; i++) {
			Element element = (Element)propertyNodes.item(i);
			String key = element.getAttribute(AppCloudIntegrationTestConstants.ATTRIBUTE_KEY);
			String value = element.getTextContent();
			keyValuePair.put(key, value);
		}
		return keyValuePair;
	}

}
