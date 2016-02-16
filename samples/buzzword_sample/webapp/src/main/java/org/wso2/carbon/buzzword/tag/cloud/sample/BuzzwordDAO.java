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

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import sun.misc.BASE64Encoder;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BuzzwordDAO {
    private static final Logger logger = Logger.getLogger(BuzzwordDAO.class.getName());

    public Buzzword[] getBuzzWordList() {

        String apiManagerUrl = System.getenv("API_MANAGER_URL");
        String apiEndpointUrl = System.getenv("API_ENDPOINT_URL");
        String apiConsumerKey = System.getenv("API_CONSUMER_KEY");
        String apiConsumerSecret = System.getenv("API_CONSUMER_SECRET");

        String values = "";

        String submitUrl = apiManagerUrl.trim()+"/token";

        logger.info("apiManagerUrl: " + apiManagerUrl);
        logger.info("apiEndpointUrl: " + apiEndpointUrl);
        logger.info("apiConsumerKey: " + apiConsumerKey);
        logger.info("apiConsumerSecret: " + apiConsumerSecret);
        String applicationToken = apiConsumerKey + ":" + apiConsumerSecret;
        logger.info("applicationToken: " + applicationToken);

        BASE64Encoder base64Encoder = new BASE64Encoder();
        applicationToken = "Bearer " + base64Encoder.encode(applicationToken.getBytes()).trim();
        logger.info("applicationToken after encoding: " + applicationToken);
        HttpClient client = new HttpClient();

        PostMethod postMethod = new PostMethod(submitUrl);
        postMethod.addRequestHeader("Authorization", applicationToken);
        postMethod.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
        postMethod.addParameter("grant_type", "client_credentials");

        int httpStatusCode = 0;
        try {

            httpStatusCode = client.executeMethod(postMethod);
            logger.info("Http status code : " + httpStatusCode);
            String accessTokenJson = "";

            if (HttpStatus.SC_OK == httpStatusCode) {
                accessTokenJson = postMethod.getResponseBodyAsString();
                logger.info("Access token response : " + accessTokenJson);
                JSONParser parser = new JSONParser();
                Object obj = parser.parse(accessTokenJson);
                JSONObject jsonObject = (JSONObject) obj;
                String accessToken = (String) jsonObject.get("access_token");
                logger.info("Access Token Received - " + accessToken);
                GetMethod apiMethod = new GetMethod(apiEndpointUrl);
                apiMethod.addRequestHeader("Authorization", "Bearer " + accessToken);

                logger.info("Api Endpoint : " + apiEndpointUrl);
                httpStatusCode = client.executeMethod(apiMethod);
                if (HttpStatus.SC_OK == httpStatusCode) {
                    logger.info("Successfully retrieved data from the api endpoint");
                    values = apiMethod.getResponseBodyAsString();
                    logger.info("data: " + values);
                } else {
                    logger.log(Level.SEVERE, "Error occurred invoking the api endpoint. Http Status : " + httpStatusCode);
                    return null;
                }

            } else {
                logger.log(Level.SEVERE, "Error occurred invoking the token endpoint \n Http status : " + httpStatusCode);
                return null;
            }

        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Error occurred", ex);
            return null;
        }

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

        if(list.size() > 0){
            return list.toArray(new Buzzword[list.size()]);
        } else {
            return null;
        }
    }
}
