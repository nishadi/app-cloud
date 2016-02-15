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
    private static final Logger logger =  Logger.getLogger(BuzzwordDAO.class.getName());

    public Buzzword[] getBuzzWordList(){

//        String apiManagerUrl = System.getenv("DB_URL");
//        String apiEndpointUrl = System.getenv("API_ENDPOINT_URL");
//        String apiConsumerKey = System.getenv("API_CONSUMER_KEY");
//        String apiConsumerSecret = System.getenv("API_CONSUMER_SECRET");
        String username = "admin";
        String password = "admin";

        String values = "";
        String accessToken = "";

        String apiManagerUrl = "https://apimanager.appfactorypreview.wso2.com/";
        String apiEndpointUrl = "http://apimanager.appfactorypreview.wso2.com:8280/yahooweather/1.0.0";
        String apiConsumerKey = "7809808jjdadaf";
        String apiConsumerSecret = "7809808jjdadaf";

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

        PostMethod method = new PostMethod(submitUrl);

        method.addRequestHeader("Authorization", applicationToken);

        method.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");

        method.addParameter("grant_type", "password");

        method.addParameter("username", username);

        method.addParameter("password", password);

//        String carbon_home = getProperty("carbon.home");
//
//        System.setProperty("javax.net.ssl.trustStore", carbon_home + "/repository/resources/security/client-truststore.jks");


        int httpStatusCode = 0;
        try {

            httpStatusCode = client.executeMethod(method);
            logger.info("Http status code : " + httpStatusCode);
            String accessTokenJson = "";

            if (HttpStatus.SC_OK == httpStatusCode) {
                logger.info("http status ok - 1");
                accessTokenJson = method.getResponseBodyAsString();
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

                } else {
                    logger.info("http status bad - 2");
                    values = "Error occurred invoking the service " + httpStatusCode;
                }

            } else {
                logger.info("http status bad - 1");
                values = "Error occurred invoking the service \n Http status : " + httpStatusCode;

            }

        } catch (Exception ex) {
            logger.info("http status bad - 3 : " + httpStatusCode);
            values = "Error occurred invoking the service \n Http status : " + ex;
        }

        List<Buzzword> list = new ArrayList<Buzzword>();
//        try {
//            DataSource dataSource = getDataSource();
//            Connection connection = dataSource.getConnection();
//
//            PreparedStatement prepStmt = connection.prepareStatement("select * from Customer");
//            ResultSet results = prepStmt.executeQuery();
//            while (results.next()) {
//                String name = results.getString("Name");
//                String region = results.getString("Region");
//                String category = results.getString("Category");
//                Customer customer = new Customer(name, category, region);
//                list.add(customer);
//            }
//            results.close();
//            prepStmt.close();
//            connection.close();
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
        return list.toArray(new Buzzword[list.size()]);
    }
}
