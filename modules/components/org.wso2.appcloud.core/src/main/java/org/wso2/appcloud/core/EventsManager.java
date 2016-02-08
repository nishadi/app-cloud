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

import org.wso2.appcloud.common.AppCloudException;
import org.wso2.appcloud.core.dao.ApplicationDAO;
import org.wso2.appcloud.core.dao.EventsDAO;
import org.wso2.appcloud.core.dto.Event;
import org.wso2.carbon.context.CarbonContext;

import java.util.List;

public class EventsManager {

    /**
     * Method for updating app creation events
     *
     * @param applicationName application name
     * @param revision application revision
     * @param event event object
     * @return
     * @throws AppCloudException
     */
    public static boolean addAppCreationEvent(String applicationName, String revision, Event event) throws AppCloudException {

        int tenantId = CarbonContext.getThreadLocalCarbonContext().getTenantId();

        ApplicationDAO applicationDAO = new ApplicationDAO();
        int applicationId = applicationDAO.getIdOfApplication(applicationName, revision, tenantId);

        EventsDAO eventsDAO = new EventsDAO();
        eventsDAO.addAppCreationEvent(applicationId, event);

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

        ApplicationDAO applicationDAO = new ApplicationDAO();
        int applicationId = applicationDAO.getIdOfApplication(applicationName, revision, tenantId);

        EventsDAO eventsDAO = new EventsDAO();
        List<Event> events = eventsDAO.getEventsOfApplication(applicationId);

        return events.toArray(new Event[events.size()]);
    }
}
