package org.wso2.carbon.mss.sample;/*
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


import java.sql.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBUtil {

    private static final Logger log = LoggerFactory.getLogger(DBUtil.class);

    public static Connection getDBConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            log.error(e.getMessage());
        }
        EnvUtil envUtil = new EnvUtil();
        String jdbcUrl = envUtil.getEnvironmentVariable("DB_URL");
        String dbUsername = envUtil.getEnvironmentVariable("DB_USERNAME");
        String dbPassword = envUtil.getEnvironmentVariable("DB_PASSWORD");

        Connection connection = null;

        try {
            connection = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword);
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
        return connection;
    }

    public static void closeConnection(Connection dbConnection) {

        if (dbConnection != null) {
            try {
                dbConnection.close();
            } catch (SQLException e) {
                String msg = "Error while closing the database connection";
                log.error(msg, e);
            }
        }
    }

    public static void closePreparedStatement(PreparedStatement preparedStatement) {

        if (preparedStatement != null) {
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                String msg = "Error while closing prepared statement";
                log.error(msg, e);
            }
        }
    }

    public static void closeStatement(Statement statement) {

        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                String msg = "Error while closing prepared statement";
                log.error(msg, e);
            }
        }
    }

    public static void rollbackTransaction(Connection dbConnection) {

        if (dbConnection != null) {
            try {
                dbConnection.rollback();
            } catch (SQLException e1) {
                log.error("Error while rolling back the failed transaction", e1);
            }
        }
    }
}
