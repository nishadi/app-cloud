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

public class SQLQueryConstants {

    /*==============================
        Database Column Constants
      ==============================*/

    public static final String ID = "id";
    public static final String APPLICATION_NAME = "application_name";
    public static final String DESCRIPTION = "description";
    public static final String TENANT_ID = "tenant_id";
    public static final String REVISION = "revision";
    public static final String APPLICATION_RUNTIME_ID = "application_runtime_id";
    public static final String APPLICATION_TYPE_ID = "application_type_id";
    public static final String APPLICATION_ENDPOINT_URL = "endpoint_url";
    public static final String APPLICATION_STATUS = "status";
    public static final String NUMBER_OF_REPLICA = "number_of_replica";
    public static final String ENDPOINT_URL_VALUE = "url_value";
    public static final String LABEL_NAME = "label_name";
    public static final String LABEL_VALUE = "label_value";
    public static final String APPLICATION_ID = "application_id";
    public static final String PROPERTY_NAME = "property_name";
    public static final String PROPERTY_VALUE = "property_value";
    public static final String APPLICATION_TYPE_NAME = "app_type_name";
    public static final String RUNTIME_NAME = "runtime_name";
    public static final String ICON = "icon";
    public static final String RUNTIME_REPO_URL = "repo_url";
    public static final String RUNTIME_IMAGE_NAME = "image_name";
    public static final String RUNTIME_TAG = "tag";

    public static final String EVENT_NAME = "event_name";
    public static final String EVENT_STATUS = "event_status";
    public static final String EVENT_TIMESTAMP = "timestamp";
    public static final String EVENT_DESCRIPTION = "event_desc";


    /*==============================
        SQL Query Constants
      ==============================*/


    /*Insert Queries*/

    public static final String ADD_APPLICATION =
            "INSERT INTO Application (application_name, description, tenant_id, revision, application_runtime_id, " +
            "application_type_iD, endpoint_url, status, number_of_replica) values (?, ?, ?, ?, ?, (SELECT id FROM " +
            "ApplicationType WHERE app_type_name=?), ?, ?, ? )";

    public static final String ADD_LABEL =
            "INSERT INTO Label (label_name, label_value, application_id, tenant_id, description) values (?, ?, ?, ?, ?)";

    public static final String ADD_RUNTIME_PROPERTY =
            "INSERT INTO RuntimeProperties (property_name, property_value, application_id, tenant_id, description) " +
            "values (?, ?, ?, ?, ?)";

    public static final String ADD_ENDPOINT_URL =
            "INSERT INTO EndpointURL (url_value, application_id, tenant_id, description) values (?, ?, ?, ?)";

    public static final String ADD_APP_CREATION_EVENT =
            "INSERT INTO ApplicationEvents (application_id, event_name, event_status, timestamp, event_desc) values (?, ?, ?, ?, ?)";


    /*Select Queries*/

    public static final String GET_ALL_APPLICATIONS_LIST =
            "SELECT MAX(Application.id) as id, Application.application_name as application_name, ApplicationRuntime.runtime_name" +
            " as runtime_name, Application.status as status, ApplicationIcon.icon as icon " +
            "FROM Application JOIN ApplicationRuntime ON " +
            "Application.application_runtime_id = ApplicationRuntime.id " +
            "LEFT OUTER JOIN ApplicationIcon ON (Application.application_name=ApplicationIcon.application_name AND " +
            "Application.tenant_id=ApplicationIcon.tenant_id) "+
            "WHERE Application.tenant_id=? GROUP BY " +
            "Application.application_name";

    public static final String GET_APPLICATION_By_NAME_REVISION =
            "SELECT Application.*, ApplicationType.app_type_name, " +
            "ApplicationRuntime.runtime_name, ApplicationIcon.icon  "+
            "FROM Application INNER JOIN ApplicationType ON Application.application_type_id=ApplicationType.id " +
            "INNER JOIN ApplicationRuntime ON Application.application_runtime_id=ApplicationRuntime.id "+
            "LEFT OUTER JOIN ApplicationIcon ON (Application.application_name=ApplicationIcon.application_name AND " +
            "Application.tenant_id=ApplicationIcon.tenant_id) "+
            "WHERE Application.application_name=? AND Application.revision=? AND Application.tenant_id=? ";

    public static final String GET_APPLICATION_ID =
            "SELECT id FROM Application WHERE application_name=? AND revision=? AND tenant_id=?";

    public static final String GET_ALL_REVISIONS_OF_APPLICATION =
            "SELECT revision from Application WHERE application_name=? AND tenant_id=?";

    public static final String GET_ALL_ENDPOINT_URL_OF_APPLICATION =
            "SELECT * FROM EndpointURL WHERE application_id=(SELECT id FROM Application WHERE application_name=? AND " +
            "revision=? AND tenant_id=?)";

    public static final String GET_ALL_ENDPOINT_URL_OF_APPLICATION_BY_ID =
            "SELECT * FROM EndpointURL WHERE application_id=?";

    public static final String GET_ALL_LABELS_OF_APPLICATION =
            "SELECT * FROM Label WHERE application_id=(SELECT id FROM Application WHERE application_name=? AND revision=?" +
            " AND tenant_id=?)";

    public static final String GET_ALL_LABELS_OF_APPLICATION_BY_ID =
            "SELECT * FROM Label WHERE application_id=?";

    public static final String UPDATE_APPLICATION_ICON =
            "INSERT INTO ApplicationIcon (icon, application_name, tenant_id) VALUES (?, ?, ?) " +
            "ON DUPLICATE KEY UPDATE icon= VALUES(icon) ";

    public static final String GET_ALL_RUNTIME_PROPERTIES_OF_APPLICATION =
            "SELECT * FROM RuntimeProperties WHERE application_id=(SELECT id FROM Application WHERE application_name=? " +
            "AND revision=? AND tenant_id=?)";

    public static final String GET_ALL_RUNTIME_PROPERTIES_OF_APPLICATION_BY_ID =
            "SELECT * FROM RuntimeProperties WHERE application_id=?";

    public static final String GET_ALL_APP_TYPES = "SELECT * FROM ApplicationType";

    public static final String GET_APP_TYPE_BY_ID = "SELECT app_type_name FROM ApplicationType WHERE id=?";

    public static final String GET_ALL_RUNTIMES_OF_TENANT =
            "SELECT * FROM ApplicationRuntime WHERE id IN (SELECT application_runtime_id FROM TenantRuntime WHERE tenant_id=?)";

    public static final String GET_RUNTIMES_FOR_APP_TYPE_OF_TENANT =
            "SELECT * FROM ApplicationRuntime WHERE id IN (SELECT application_runtime_id FROM ApplicationTypeRuntime WHERE " +
            "application_type_id=(SELECT id FROM ApplicationType WHERE app_type_name=?))";

    public static final String GET_RUNTIME_FOR_APP_TYPE =
            "SELECT * FROM ApplicationRuntime WHERE id = ?";

    public static final String GET_ALL_EVENTS_OF_APPLICATION = "select * from ApplicationEvents A "
            + "where A.application_id = ? "
            + "and A.id >= (select MAX(B.id) from ApplicationEvents B where B.application_id = A.application_id and B.event_name = A.event_name);";

    /*Update Queries*/

    public static final String UPDATE_RUNTIME_PROPERTIES =
            "UPDATE RuntimeProperties SET property_name=?, property_value=? WHERE application_id=? AND tenant_id=? AND property_name=? AND property_value=?";
    public static final String UPDATE_TAG =
            "UPDATE Label SET label_name=?, label_value=? WHERE application_id=? AND tenant_id=? AND label_name=? AND label_value=?";

    public static final String UPDATE_APPLICATION_STATUS =
            "UPDATE Application SET status=? WHERE application_name=? AND revision=? AND tenant_id=?";

    public static final String UPDATE_NUMBER_OF_REPLICA =
            "UPDATE Application SET number_of_replica=? WHERE application_name=? AND revision=? AND tenant_id=?";

    /*Delete Queries*/
    public static final String DELETE_RUNTIME_PROPERTIES =
            "DELETE FROM RuntimeProperties WHERE application_id=? AND tenant_id=? AND property_name=? AND property_value=?";
    public static final String DELETE_TAG =
            "DELETE FROM Label WHERE application_id=? AND tenant_id=? AND label_name=? AND label_value=?";

    public static final String DELETE_APPLICATION = "DELETE FROM Application WHERE id = ?";

    public static final String DELETE_APPLICATION_REVISION = "DELETE FROM Application "
            + "WHERE application_name = ? and tenant_id = ?";
}
