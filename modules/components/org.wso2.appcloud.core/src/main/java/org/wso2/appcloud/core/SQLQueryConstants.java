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
    public static final String NAME = "name";
    public static final String VALUE = "value";
    public static final String IS_SECURED = "is_secured";
    public static final String REPLICAS = "replicas";
    public static final String VERSION = "version";
    public static final String PROTOCOL = "protocol";
    public static final String PORT = "port";
    public static final String BACKEND_PORT = "backend_port";
    public static final String DESCRIPTION = "description";
    public static final String HASH_ID = "hash_id";
    public static final String DEFAULT_VERSION = "default_version";
    public static final String RUNTIME_ID = "runtime_id";
    public static final String STATUS = "status";
    public static final String APPLICATION_TYPE_NAME = "app_type_name";
    public static final String BUILDABLE = "buildable" ;
    public static final String RUNTIME_NAME = "runtime_name";
    public static final String ICON = "icon";
    public static final String RUNTIME_REPO_URL = "repo_url";
    public static final String RUNTIME_IMAGE_NAME = "image_name";
    public static final String RUNTIME_TAG = "tag";
    public static final String EVENT_TIMESTAMP = "timestamp";


    /*==============================
        SQL Query Constants
      ==============================*/


    /*Insert Queries*/

    public static final String ADD_APPLICATION =
            "INSERT INTO AC_APPLICATION (name, hash_id, description, tenant_id, default_version, app_type_id) values " +
            "(?, ?, ?, ?, ?, (SELECT id FROM AC_APP_TYPE WHERE name=?))";

    public static final String ADD_VERSION =
            "INSERT INTO AC_VERSION (name, hash_id, application_id, runtime_id, tenant_id) VALUES (?, ?, ?, ?, ?)";

    public static final String ADD_TAG =
            "INSERT INTO AC_TAG (name, value, version_id, description, tenant_id) values (?, ?, (SELECT id FROM " +
            "AC_VERSION WHERE hash_id=?), ?, ?)";

    public static final String ADD_RUNTIME_PROPERTY =
            "INSERT INTO AC_RUNTIME_PROPERTY (name, value, version_id, description, tenant_id, is_secured) values " +
            "(?, ?, (SELECT id FROM AC_VERSION WHERE hash_id=?), ?, ?, ?)";

    public static final String ADD_APP_CREATION_EVENT =
            "INSERT INTO AC_EVENT (name, status, version_id, timestamp, description, tenant_id) values (?, ?, (SELECT id" +
            " FROM AC_VERSION WHERE hash_id=?), ?, ?, ?)";

    public static final String ADD_DEPLOYMENT =
            "INSERT INTO AC_DEPLOYMENT (name, replicas, tenant_id) values (?, ?, ?)";

    public static final String ADD_CONTAINER =
            "INSERT INTO AC_CONTAINER (name, version, deployment_id, tenant_id) values (?, ?, ?, ?)";

    public static final String ADD_CONTAINER_SERVICE_PROXY =
            "INSERT INTO AC_CONTAINER_SERVICE_PROXY (name, protocol, port, backend_port, container_id, tenant_id) "
                    + "values (?, ?, ?, ?, ?, ?)";

    public static final String ADD_CUSTOM_APP_TRANSPORT =
            "INSERT INTO AC_TRANSPORT_CUSTOM (version_hash_id, name, port, protocol) VALUES (?, ?, ?, ?)";



    /*Select Queries*/

    public static final String GET_ALL_APPLICATIONS_LIST =
            "SELECT app.name as application_name, app.hash_id as hash_id, type.name as app_type_name, icon.icon as icon " +
            "FROM AC_APPLICATION app JOIN AC_APP_TYPE type ON app.app_type_id = type.id LEFT OUTER JOIN AC_APP_ICON icon" +
            " ON app.id = icon.application_id WHERE app.tenant_id=?";

    public static final String GET_VERSION_LIST_OF_APPLICATION =
            "SELECT name FROM AC_VERSION WHERE application_id = (SELECT id FROM AC_APPLICATION WHERE hash_id=?)";

    public static final String GET_VERSION_HASH_IDS_OF_APPLICATION =
            "SELECT hash_id FROM AC_VERSION WHERE application_id = (SELECT id FROM AC_APPLICATION WHERE hash_id=?)";

    public static final String GET_VERSION_HASH_IDS_OF_APPLICATION_BY_VERSION_HASH_ID =
            "SELECT hash_id FROM AC_VERSION WHERE application_id = (SELECT application_id FROM AC_VERSION WHERE hash_id=?)";

    public static final String GET_APPLICATION_HASH_ID_BY_VERSION_HASH_ID =
            "SELECT hash_id FROM AC_APPLICATION WHERE id = (SELECT application_id FROM AC_VERSION WHERE hash_id=?)";

    public static final String GET_APPLICATION_BY_HASH_ID =
            "SELECT app.*, type.name as app_type_name, icon.icon as icon FROM AC_APPLICATION app JOIN AC_APP_TYPE type " +
            "ON app.app_type_id = type.id JOIN AC_APP_ICON icon ON app.id = icon.application_id WHERE app.hash_id=?";

    public static final String GET_ALL_VERSIONS_OF_APPLICATION =
            "SELECT version.*, runtime.name as runtime_name, runtime.id as runtime_id FROM AC_VERSION version JOIN " +
            "AC_RUNTIME runtime ON version.runtime_id = runtime.id WHERE version.application_id = (SELECT id FROM " +
            "AC_APPLICATION WHERE hash_id=?)";

    public static final String GET_APPLICATION_NAME_BY_HASH_ID =
            "SELECT name FROM AC_APPLICATION WHERE hash_id = ?";

    public static final String GET_APPLICATION_HASH_ID_BY_NAME =
            "SELECT hash_id FROM AC_APPLICATION WHERE name=? AND tenant_id=?";

    public static final String GET_APPLICATION_ID =
            "SELECT id FROM AC_APPLICATION WHERE hash_id=?";

    public static final String GET_VERSION_ID =
            "SELECT id FROM AC_VERSION WHERE hash_id=?";

    public static final String GET_ALL_TAGS_OF_VERSION =
            "SELECT * FROM AC_TAG WHERE version_id=(SELECT id FROM AC_VERSION WHERE hash_id=?)";

    public static final String GET_ALL_RUNTIME_PROPERTIES_OF_VERSION =
            "SELECT * FROM AC_RUNTIME_PROPERTY WHERE version_id=(SELECT id FROM AC_VERSION WHERE hash_id=?)";

    public static final String GET_CONTAINER_SERVICE_PROXIES =
            "SELECT * FROM AC_CONTAINER_SERVICE_PROXY WHERE container_id=?";

    public static final String GET_TRANSPORTS_FOR_RUNTIME =
            "SELECT name, port, protocol FROM AC_TRANSPORT WHERE id IN (SELECT transport_id FROM AC_RUNTIME_TRANSPORT " +
            "WHERE runtime_id=?)";

    public static final String GET_TRANSPORTS_FOR_CUSTOM_APPLICATION =
            "SELECT name, port, protocol FROM AC_TRANSPORT_CUSTOM WHERE name=? AND version_hash_id=?";

    public static final String GET_ALL_APP_TYPES = "SELECT * FROM AC_APP_TYPE";

    public static final String GET_RUNTIMES_FOR_APP_TYPE_OF_TENANT =
            "SELECT * FROM AC_RUNTIME WHERE id IN (SELECT runtime_id FROM AC_APP_TYPE_RUNTIME WHERE app_type_id=" +
            "(SELECT id FROM AC_APP_TYPE WHERE name=?))";

    public static final String GET_RUNTIME_BY_ID =
            "SELECT * FROM AC_RUNTIME WHERE id = ?";

    public static final String GET_ALL_EVENTS_OF_APPLICATION =
            "select * from AC_EVENT A where A.version_id = (SELECT id FROM AC_VERSION WHERE hash_id=?) and A.id >= " +
            "(select MAX(B.id) from AC_EVENT B where B.version_id = A.version_id and B.name = A.name)";

    public static final String GET_DEPLOYMENT =
            "SELECT * from AC_DEPLOYMENT where id=(SELECT deployment_id from AC_VERSION WHERE hash_id=?)";

    public static final String GET_CONTAINER =
            "SELECT * FROM AC_CONTAINER WHERE deployment_id=?";



    /* Update Queries */

    public static final String UPDATE_APPLICATION_ICON =
            "INSERT INTO AC_APP_ICON (icon, application_id) VALUES (?, (SELECT id FROM AC_APPLICATION WHERE " +
            "hash_id=?)) ON DUPLICATE KEY UPDATE icon= VALUES(icon)";

    public static final String UPDATE_RUNTIME_PROPERTIES =
            "UPDATE AC_RUNTIME_PROPERTY SET name=?, value=? WHERE version_id=(SELECT id FROM AC_VERSION WHERE hash_id=?)" +
            " AND name=?";

    public static final String UPDATE_VERSION_WITH_DEPLOYMENT =
            "UPDATE AC_VERSION SET deployment_id=? WHERE hash_id=? AND tenant_id=?";

    public static final String UPDATE_TAG =
            "UPDATE AC_TAG SET name=?, value=? WHERE version_id=(SELECT id FROM AC_VERSION WHERE hash_id=?) AND name=? ";

    public static final String UPDATE_APPLICATION_STATUS =
            "UPDATE AC_VERSION SET status=? WHERE hash_id=?";




    /*Delete Queries*/

    public static final String DELETE_RUNTIME_PROPERTY =
            "DELETE FROM AC_RUNTIME_PROPERTY WHERE version_id=(SELECT id FROM AC_VERSION WHERE hash_id=?) AND name=?";

    public static final String DELETE_TAG =
            "DELETE FROM AC_TAG WHERE version_id=(SELECT id FROM AC_VERSION WHERE hash_id=?) AND name=?";

    public static final String DELETE_APPLICATION = "DELETE FROM AC_APPLICATION WHERE hash_id=?";

    public static final String DELETE_VERSION = "DELETE FROM AC_VERSION WHERE hash_id=?";

    public static final String DELETE_VERSIONS_OF_APPLICATION =
            "DELETE FROM AC_VERSION WHERE application_id = (SELECT id FROM AC_APPLICATION WHERE hash_id=?)";

    public static final String DELETE_DEPLOYMENT =
            "DELETE FROM AC_DEPLOYMENT WHERE id=(SELECT deployment_id FROM AC_VERSION WHERE hash_id=?)";

    public static final String DELETE_ALL_DEPLOYMENT_OF_APPLICATION =
            "DELETE FROM AC_DEPLOYMENT WHERE id in (SELECT deployment_id from AC_VERSION WHERE application_id = " +
            "(SELECT id FROM AC_APPLICATION WHERE hash_id=?))";

    public static final String DELETE_ALL_APP_VERSION_EVENTS =
            "Delete from AC_EVENT where version_id = (SELECT id FROM AC_VERSION WHERE hash_id=?)";

}
