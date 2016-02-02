/*
* Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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


package org.wso2.appcloud.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.appcloud.common.AppCloudException;
import org.wso2.appcloud.common.util.AppCloudUtil;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DBUtil {

    private static Log log = LogFactory.getLog(DBUtil.class);

    private static DataSource dataSource;
    private static final String DATASOURCE_NAME = "DataSourceName";

    static {
        try {
            String datasourceName = AppCloudUtil.getPropertyValue(DATASOURCE_NAME);

            InitialContext context = new InitialContext();
            dataSource = (DataSource)context.lookup(datasourceName);

            if(log.isDebugEnabled()){
                log.debug("Initialized datasource : " + datasourceName + " successfully");
            }

        } catch (NamingException e) {
            log.error("Error while initializing datasource : " + DATASOURCE_NAME, e);
            throw new ExceptionInInitializerError(e);
        }
    }

    public static Connection getDBConnection() throws AppCloudException {

        Connection connection;
        try {

            connection = dataSource.getConnection();
            connection.setAutoCommit(false);

        } catch (SQLException e) {
            String msg = "Error while getting  database connection";
            throw new AppCloudException(msg, e);
        }

        return connection;
    }

    public static void closeConnection(Connection dbConnection){

        if(dbConnection != null){
            try {
                dbConnection.close();
            } catch (SQLException e) {
                String msg = "Error while closing the database connection";
                log.error(msg, e);
            }
        }
    }

    public static void closePreparedStatement(PreparedStatement preparedStatement){

        if(preparedStatement != null){
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                String msg = "Error while closing prepared statement";
                log.error(msg, e);
            }
        }
    }

    public static void rollbackTransaction(Connection dbConnection){

        if(dbConnection != null){
            try {
                dbConnection.rollback();
            } catch (SQLException e1) {
                log.error("Error while rolling back the failed transaction", e1);
            }
        }
    }
}
