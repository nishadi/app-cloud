package org.wso2.carbon.buzzword.tag.cloud.sample;
/*
*Copyright (c) 2005-2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*WSO2 Inc. licenses this file to you under the Apache License,
*Version 2.0 (the "License"); you may not use this file except
*in compliance with the License.
*You may obtain a copy of the License at
*
*http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing,
*software distributed under the License is distributed on an
*"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*KIND, either express or implied.  See the License for the
*specific language governing permissions and limitations
*under the License.
*/

import sun.misc.BASE64Encoder;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.HttpStatus;

import java.util.logging.Logger;

import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BuzzwordDAO {
    private static final Logger logger = Logger.getLogger(BuzzwordDAO.class.getName());

    public Buzzword[] getBuzzWordList() {

        String apiManagerUrl = System.getenv("API_MANAGER_URL");
        String apiEndpointUrl = System.getenv("API_ENDPOINT_URL");
        String apiConsumerKey = System.getenv("API_CONSUMER_KEY");
        String apiConsumerSecret = System.getenv("API_CONSUMER_SECRET");
        String username = System.getenv("TENANT_USERNAME");
        String password = System.getenv("TENANT_PASSWORD");

//        String apiManagerUrl = "http://172.17.0.1:9443";
//        String apiEndpointUrl = "http://172.17.0.1:8280/buzzword/1.0.0/all";
//        String apiConsumerKey = "YaBFwIgxfEPDzicCgkC8zlrcyHsa";
//        String apiConsumerSecret = "QuJ2dEvTwdLe3q0ciXtD3ECu0O4a";
//        String username = "admin";
//        String password = "admin";

        String values = "";
        String accessToken = "";

        String submitUrl = apiManagerUrl.trim()+"/oauth2/token";

        logger.info("apiManagerUrl: " + apiManagerUrl);
        logger.info("apiEndpointUrl: " + apiEndpointUrl);
        logger.info("apiConsumerKey: " + apiConsumerKey);
        logger.info("apiConsumerSecret: " + apiConsumerSecret);

        String applicationToken = apiConsumerKey + ":" + apiConsumerSecret;
        logger.info("applicationToken: " + applicationToken);

        BASE64Encoder base64Encoder = new BASE64Encoder();
        applicationToken = "Basic " + base64Encoder.encode(applicationToken.getBytes()).trim();
        logger.info("applicationToken after encoding: " + applicationToken);

        HttpClient client = new HttpClient();

        PostMethod postMethod = new PostMethod(submitUrl);
        postMethod.addRequestHeader("Authorization", applicationToken);
        postMethod.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
        postMethod.addParameter("grant_type", "password");
        postMethod.addParameter("username", username);
        postMethod.addParameter("password", password);

//        String carbon_home = getProperty("carbon.home");
//
//        System.setProperty("javax.net.ssl.trustStore", carbon_home + "/repository/resources/security/client-truststore.jks");


        int httpStatusCode = 0;
        try {

            httpStatusCode = client.executeMethod(postMethod);
            logger.info("Http status code : " + httpStatusCode);
            String accessTokenJson = "";

            if (HttpStatus.SC_OK == httpStatusCode) {
                logger.info("http status ok - 1");
                accessTokenJson = postMethod.getResponseBodyAsString();
                logger.info("Json : " + accessTokenJson);
                JSONParser parser = new JSONParser();
                Object obj = parser.parse(accessTokenJson);
                JSONObject jsonObject = (JSONObject) obj;

                accessToken = (String) jsonObject.get("access_token");
                String refreshToken = (String) jsonObject.get("refresh_token");
                logger.info("Access Token Received - " + accessToken);
                GetMethod apiMethod = new GetMethod(apiEndpointUrl);
                apiMethod.addRequestHeader("Authorization", "Bearer " + accessToken);

                logger.info("Api Endpoint : " + apiEndpointUrl);
                httpStatusCode = client.executeMethod(apiMethod);
                if (HttpStatus.SC_OK == httpStatusCode) {
                    logger.info("http status ok - 2");
                    values = apiMethod.getResponseBodyAsString();
                    logger.info("***************" + values);

                } else {
                    logger.info("http status bad - 2");
                    values = "Error occurred invoking the service " + httpStatusCode;
                    logger.info(values);
                }

            } else {
                logger.info("http status bad - 1");
                values = "Error occurred invoking the service \n Http status : " + httpStatusCode;
                logger.info(values);
            }

        } catch (Exception ex) {
            logger.info("http status bad - 3 : " + httpStatusCode);
            values = "Error occurred invoking the service \n Http status : " + ex;
            logger.info(values);
        }


        //values = {Eclipse=1, Hava=1, J2EE=3}
        List<Buzzword> list = new ArrayList<Buzzword>();
        String[] elements = values.split(",");
        for (String element : elements) {


            String words = element.split("=")[0].trim();
            String ranks = element.split("=")[1].trim();
            if (element.contains("{")) {
                words = words.replace("{", "");
            }
            if (element.contains("}")) {
                ranks = ranks.replace("}", "");
            }
            logger.info("words and ranks: " + words + " " + ranks);
            Buzzword buzzword = new Buzzword(words, Integer.parseInt(ranks)*5);
            list.add(buzzword);
        }

        return list.toArray(new Buzzword[list.size()]);
    }
}
