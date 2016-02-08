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

package org.wso2.appcloud.core.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.appcloud.common.AppCloudException;
import org.wso2.appcloud.core.DBUtil;
import org.wso2.appcloud.core.SQLQueryConstants;
import org.wso2.appcloud.core.dto.Application;
import org.wso2.appcloud.core.dto.Event;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * DAO class for persisting or retrieving application creation related events
 */

public class EventsDAO {

    private static final Log log = LogFactory.getLog(EventsDAO.class);

    /**
     * Method for adding application creation events to database
     *
     * @param applicationId application id
     * @param event application creation event
     * @return
     * @throws AppCloudException
     */
    public boolean addAppCreationEvent(int applicationId, Event event) throws AppCloudException {

        Connection dbConnection = DBUtil.getDBConnection();
        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.ADD_APP_CREATION_EVENT);
            preparedStatement.setInt(1, applicationId);
            preparedStatement.setString(2, event.getEventName());
            preparedStatement.setString(3, event.getEventStatus());
            preparedStatement.setTimestamp(4, event.getTimestamp());
            preparedStatement.setString(5, event.getEventDescription());

            boolean result = preparedStatement.execute();
            dbConnection.commit();
            log.info("DB insert query result: " + result);

        } catch (SQLException e) {
            String msg = "Error occurred while adding app creation event: " + event.getEventName() + " status: " + event
                    .getEventStatus() + " timestamp: " + event.getTimestamp();
            log.error(msg, e);
            throw new AppCloudException(msg, e);

        } finally {
            DBUtil.closePreparedStatement(preparedStatement);
            DBUtil.closeConnection(dbConnection);
        }
        return true;
    }

    /**
     *  Method to get event stream of an application
     *
     * @param applicationId application id
     * @return
     * @throws AppCloudException
     */
    public List<Event> getEventsOfApplication(int applicationId) throws AppCloudException {

        Connection dbConnection = DBUtil.getDBConnection();
        PreparedStatement preparedStatement = null;

        List<Event> eventList = new ArrayList<>();

        try {
            preparedStatement = dbConnection.prepareStatement(SQLQueryConstants.GET_ALL_EVENTS_OF_APPLICATION);
            preparedStatement.setInt(1, applicationId);

            ResultSet resultSet = preparedStatement.executeQuery();
            Event event;
            while (resultSet.next()) {
                event = new Event();
                event.setEventName(resultSet.getString(SQLQueryConstants.EVENT_NAME));
                event.setEventStatus(resultSet.getString(SQLQueryConstants.EVENT_STATUS));
                event.setTimestamp(resultSet.getTimestamp(SQLQueryConstants.EVENT_TIMESTAMP));
                event.setEventDescription(resultSet.getString(SQLQueryConstants.EVENT_DESCRIPTION));

                eventList.add(event);
            }

        } catch (SQLException e) {
            String msg = "Error while retrieving Application creation event stream for applicationId: " + applicationId;
            log.error(msg, e);
            throw new AppCloudException(msg, e);
        } finally {
            DBUtil.closePreparedStatement(preparedStatement);
            DBUtil.closeConnection(dbConnection);
        }
        return eventList;
    }
}
