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
import org.wso2.appcloud.core.dao.EventsDAO;
import org.wso2.appcloud.core.dto.Application;
import org.wso2.appcloud.core.dto.Event;
import org.wso2.carbon.context.CarbonContext;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class EventsManager {

    private static Log log = LogFactory.getLog(ApplicationManager.class);

    /**
     * Method for updating app creation events
     *
     * @param application application object
     * @param event event object
     * @return
     * @throws AppCloudException
     */
    public static boolean addAppCreationEvent(Application application, Event event) throws AppCloudException {

        EventsDAO eventsDAO = new EventsDAO();
        Connection dbConnection = DBUtil.getDBConnection();

        try {
            eventsDAO.addAppCreationEvent(application, event);
            dbConnection.commit();

        } catch (AppCloudException e) {

            DBUtil.rollbackTransaction(dbConnection);

            String msg = "Error occurred while adding application event: " + event.getEventName() + "for application: "
                    + application.getApplicationName();
            log.error(msg, e);
            throw new AppCloudException(msg, e);

        } catch (SQLException e) {

            DBUtil.rollbackTransaction(dbConnection);

            String msg = "Error while committing the App Creation Event: " + event.getEventName()
                    + " transaction for application : " + application.getApplicationName();
            log.error(msg, e);
            throw new AppCloudException(msg, e);

        } finally {
            DBUtil.closeConnection(dbConnection);
        }

        return true;
    }

    /**
     * Method for retrieve application creation event stream
     *
     * @param applicationName application name
     * @param revision application revision
     * @return
     * @throws AppCloudException
     */
    public Event[] getEventsOfApplication(String applicationName, String revision) throws AppCloudException {
        int tenantId = CarbonContext.getThreadLocalCarbonContext().getTenantId();
        EventsDAO eventsDAO = new EventsDAO();
        List<Event> events = eventsDAO.getEventsOfApplication(applicationName, revision, tenantId);
        return events.toArray(new Event[events.size()]);
    }
}
