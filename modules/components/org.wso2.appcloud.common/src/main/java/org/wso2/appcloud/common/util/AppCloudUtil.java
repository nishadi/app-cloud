package org.wso2.appcloud.common.util;
/*
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

import org.apache.axiom.util.base64.Base64Utils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.appcloud.common.AppCloudConstant;
import org.wso2.appcloud.common.AppCloudException;
import org.wso2.carbon.base.MultitenantConstants;
import org.wso2.carbon.utils.CarbonUtils;

import java.io.*;
import java.security.SignatureException;
import java.security.interfaces.RSAPrivateKey;
import java.util.Properties;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.wso2.carbon.core.util.KeyStoreManager;


/**
 * This class is responsible for keeping utils method which needs for other modules
 */
public class AppCloudUtil {

    private static Properties properties = new Properties();
    private static final Log log = LogFactory.getLog(AppCloudUtil.class);

    static {
        try {
            loadAppCloudConfig();
        } catch (AppCloudException e) {
            String message = "Unable to load AppCloud configuration file";
            log.error(message, e);
        }
    }

    /**
     * Load key and value from the AppCloud.property file
     *
     * @throws AppCloudException
     */
    private static void loadAppCloudConfig() throws AppCloudException {
        String fileLocation = new StringBuilder().append(CarbonUtils.getCarbonConfigDirPath()).append(File.separator)
                .append(AppCloudConstant.CONFIG_FOLDER).append(File.separator).append(AppCloudConstant.CONFIG_FILE_NAME)
                .toString();
        File file = new File(fileLocation);
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            properties.load(inputStream);
        } catch (FileNotFoundException e) {
            String message = "The AppCloud.properties file not found from file location: " + fileLocation;
            throw new AppCloudException(message, e);
        } catch (IOException e) {
            String message = "Unable to read AppCloud.properties file from file location: " + fileLocation;
            throw new AppCloudException(message, e);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                String message = "Unable to close file input stream while reading appcloud configuration";
                log.error(message, e);
            }
        }

    }

    /**
     * Get a value from given property
     *
     * @param property
     * @return
     */
    public static String getPropertyValue(String property) {
        String value = properties.getProperty(property);
        if (value == null) {
            String message = "The given property: " + property + " is not found from the AppCloud.properties file";
            log.warn(message);
        }
        return value;
    }

    public static String getAuthHeader(String username) throws AppCloudException {

        //Get the filesystem keystore default primary certificate
        KeyStoreManager keyStoreManager;
        keyStoreManager = KeyStoreManager.getInstance(MultitenantConstants.SUPER_TENANT_ID);
        try {
            keyStoreManager.getDefaultPrimaryCertificate();
            JWSSigner signer = new RSASSASigner((RSAPrivateKey) keyStoreManager.getDefaultPrivateKey());
            JWTClaimsSet claimsSet = new JWTClaimsSet();
            claimsSet.setClaim(AppCloudConstant.SIGNED_JWT_AUTH_USERNAME, username);
            SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.RS512), claimsSet);
            signedJWT.sign(signer);

            // generate authorization header value
            return "Bearer " + Base64Utils.encode(signedJWT.serialize().getBytes());
        } catch (SignatureException e) {
            String msg = "Failed to sign with signature instance";
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } catch (Exception e) {
            String msg = "Failed to get primary default certificate";
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        }
    }

}
