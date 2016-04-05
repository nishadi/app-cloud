#!/bin/bash
# This is a basic script to get a developer setup done with multi-tenancy and sso
# Update the pack_dir and setup_dir and run. it will drop existing databases and setup 
if [[ $# -eq 0 ]] ; then
    echo 'Usage: ./setup.sh /srv/app_cloud'
    exit 1
fi
APPCLOUD_HOME=$1
PACK_DIR=$APPCLOUD_HOME/packs
SETUP_DIR=$APPCLOUD_HOME/setup
AS_VERSION=wso2as-5.2.1
IS_VERSION=wso2is-5.0.0
SS_VERSION=wso2ss-1.1.0
DAS_VERSION=wso2das-3.0.1

IP="$(ifconfig | grep -A 1 'eth0' | tail -1 | cut -d ':' -f 2 | cut -d ' ' -f 1)"

APP_CLOUD_SRC_HOME=`pwd`/../../

# Build source code
mvn clean install -Dmaven.test.skip=true -f $APP_CLOUD_SRC_HOME/pom.xml

# Setting up default carbon database
MYSQL=`which mysql`
Q0="DROP DATABASE IF EXISTS cloudUserstore;"
Q1="CREATE DATABASE cloudUserstore;"
SQL="${Q0}${Q1}"
$MYSQL -uroot -proot -A -e "$SQL";

# Setting up app cloud database
Q2="DROP DATABASE IF EXISTS AppCloudDB;"
SQL1="${Q2}"
$MYSQL -uroot -proot -A -e "$SQL1";
$MYSQL -uroot -proot < $APP_CLOUD_SRC_HOME/modules/dbscripts/appcloud.sql

# Setting up storage server databases
Q3="DROP DATABASE IF EXISTS rss_db;"
Q4="CREATE DATABASE rss_db;"
SQL2="${Q3}${Q4}"
$MYSQL -uroot -proot -A -e "$SQL2";

#Setting up http monitoring dataase
Q5="DROP DATABASE IF EXISTS analytics_event_store;"
Q6="CREATE DATABASE analytics_event_store;"
Q7="DROP DATABASE IF EXISTS analytics_processed_data_store;"
Q8="CREATE DATABASE analytics_processed_data_store;"
SQL1="${Q5}${Q6}${Q7}${Q8}"
$MYSQL -uroot -proot -A -e "$SQL1";
$MYSQL -uroot -proot < $APP_CLOUD_SRC_HOME/modules/dbscripts/http-mon-mysql.sql

# Unzip default wso2carbon product packs and configure
mkdir -p $SETUP_DIR
unzip -q $PACK_DIR/$IS_VERSION.zip -d $SETUP_DIR/
unzip -q $PACK_DIR/$SS_VERSION.zip -d $SETUP_DIR/
unzip -q $PACK_DIR/$DAS_VERSION.zip -d $SETUP_DIR/

IS_HOME=$SETUP_DIR/$IS_VERSION/
SS_HOME=$SETUP_DIR/$SS_VERSION/
DAS_HOME=$SETUP_DIR/$DAS_VERSION/


function as_setup(){
    mkdir -p $1/repository/deployment/server/jaggeryapps/appmgt/
    unzip -q $APP_CLOUD_SRC_HOME/modules/jaggeryapps/appmgt/target/appmgt-3.0.0-SNAPSHOT.zip -d $1/repository/deployment/server/jaggeryapps/appmgt/
    sed -e "s@AS_HOME@$1@g" $APP_CLOUD_SRC_HOME/modules/setup-scripts/jaggery/site.json > $1/repository/deployment/server/jaggeryapps/appmgt/site/conf/site.json
    cp -R $APP_CLOUD_SRC_HOME/modules/resources/dockerfiles $1/repository/deployment/server/jaggeryapps/appmgt/

    cp -r $APP_CLOUD_SRC_HOME/modules/setup-scripts/jaggery/modules/* $1/modules/

    cp $APP_CLOUD_SRC_HOME/modules/setup-scripts/lib/org.wso2.carbon.hostobjects.sso_4.2.0.jar $1/repository/components/dropins/
    cp $APP_CLOUD_SRC_HOME/modules/setup-scripts/lib/nimbus-jose-jwt_2.26.1.wso2v2.jar $1/repository/components/dropins/
    cp $APP_CLOUD_SRC_HOME/modules/setup-scripts/lib/commons-codec-1.10.wso2v1.jar $1/repository/components/dropins/
    cp $APP_CLOUD_SRC_HOME/modules/setup-scripts/lib/commons-compress-1.9.0.wso2v1.jar $1/repository/components/dropins/

    cp $APP_CLOUD_SRC_HOME/modules/setup-scripts/lib/docker-client-1.0.10.wso2v1.jar $1/repository/components/dropins/
    cp $APP_CLOUD_SRC_HOME/modules/setup-scripts/lib/jackson-annotations-2.7.3.jar $1/repository/components/dropins/
    cp $APP_CLOUD_SRC_HOME/modules/setup-scripts/lib/jackson-core-2.7.3.jar $1/repository/components/dropins/
    cp $APP_CLOUD_SRC_HOME/modules/setup-scripts/lib/jackson-databind-2.7.3.jar $1/repository/components/dropins/
    cp $APP_CLOUD_SRC_HOME/modules/setup-scripts/lib/jackson-dataformat-yaml-2.7.3.jar $1/repository/components/dropins/
    cp $APP_CLOUD_SRC_HOME/modules/setup-scripts/lib/slf4j-api-1.7.12.wso2v1.jar $1/repository/components/dropins/
    cp $APP_CLOUD_SRC_HOME/modules/setup-scripts/lib/snakeyaml-1.15.jar $1/repository/components/dropins/

    cp $APP_CLOUD_SRC_HOME/modules/setup-scripts/lib/mysql-connector-java-5.1.27-bin.jar $1/repository/components/lib/

    cp $APP_CLOUD_SRC_HOME/modules/setup-scripts/lib/junixsocket-common-2.0.4.wso2v1.jar $1/repository/components/dropins/
    cp $APP_CLOUD_SRC_HOME/modules/setup-scripts/lib/logging-interceptor-2.7.2.wso2v1.jar $1/repository/components/dropins/
    cp $APP_CLOUD_SRC_HOME/modules/setup-scripts/lib/okhttp-2.7.2.wso2v1.jar $1/repository/components/dropins/
    cp $APP_CLOUD_SRC_HOME/modules/setup-scripts/lib/okhttp-ws-2.7.2.wso2v1.jar $1/repository/components/dropins/
    cp $APP_CLOUD_SRC_HOME/modules/setup-scripts/lib/okio-1.6.0.wso2v1.jar $1/repository/components/dropins/
    cp $APP_CLOUD_SRC_HOME/modules/setup-scripts/lib/validation-api-1.1.0.Final.jar $1/repository/components/dropins/

    cp $APP_CLOUD_SRC_HOME/modules/setup-scripts/lib/kubernetes-client-1.3.76.wso2v1.jar $1/repository/components/dropins/
    cp $APP_CLOUD_SRC_HOME/modules/setup-scripts/lib/dnsjava-2.1.7.wso2v1.jar $1/repository/components/dropins/
    cp $APP_CLOUD_SRC_HOME/modules/setup-scripts/lib/json-20160212.jar $1/repository/components/dropins/
    cp $APP_CLOUD_SRC_HOME/modules/setup-scripts/lib/fabric8-utils-2.2.100.jar $1/repository/components/dropins/    

    cp $APP_CLOUD_SRC_HOME/modules/setup-scripts/conf/wso2as-5.2.1/repository/conf/datasources/master-datasources.xml $1/repository/conf/datasources/
    cp $APP_CLOUD_SRC_HOME/modules/setup-scripts/conf/wso2as-5.2.1/repository/conf/datasources/appcloud-datasources.xml $1/repository/conf/datasources/
    cp $APP_CLOUD_SRC_HOME/modules/components/org.wso2.appcloud.core/target/org.wso2.appcloud.core-3.0.0-SNAPSHOT.jar $1/repository/components/dropins/
    cp $APP_CLOUD_SRC_HOME/modules/components/org.wso2.appcloud.provisioning.runtime/target/org.wso2.appcloud.provisioning.runtime-3.0.0-SNAPSHOT.jar $1/repository/components/dropins/
    cp $APP_CLOUD_SRC_HOME/modules/components/org.wso2.appcloud.common/target/org.wso2.appcloud.common-3.0.0-SNAPSHOT.jar $1/repository/components/dropins/
    mkdir -p $1/repository/conf/appcloud
    cp $APP_CLOUD_SRC_HOME/modules/setup-scripts/conf/wso2as-5.2.1/repository/conf/appcloud/appcloud.properties $1/repository/conf/appcloud/
    sed -i -e "s|AS_HOME|$1|g" $1/repository/conf/appcloud/appcloud.properties
}
function as_cluster_setup(){

    mkdir -p $SETUP_DIR/AS_NODE1
    mkdir -p $SETUP_DIR/AS_NODE2
    unzip -q $PACK_DIR/$AS_VERSION.zip -d $SETUP_DIR/AS_NODE1/
    unzip -q $PACK_DIR/$AS_VERSION.zip -d $SETUP_DIR/AS_NODE2/

    AS_HOME1=$SETUP_DIR/AS_NODE1/$AS_VERSION/
    AS_HOME2=$SETUP_DIR/AS_NODE2/$AS_VERSION/

    echo "Starting AS Node 1 setup.."
    as_setup $AS_HOME1
    echo "AS Node 1 setup done successfully!"
    echo "Starting AS Node 2 setup.."
    as_setup $AS_HOME2
    echo "AS Node2 setup done successfully!"

    cp $APP_CLOUD_SRC_HOME/modules/setup-scripts/conf/wso2as-5.2.1/repository/conf/carbon.xml $AS_HOME1/repository/conf/
    cp $APP_CLOUD_SRC_HOME/modules/setup-scripts/conf/wso2as-5.2.1/repository/conf/carbon.xml $AS_HOME2/repository/conf/
    sed -i -e "s/<Offset>0<\/Offset>/<Offset>4<\/Offset>/g" $AS_HOME2/repository/conf/carbon.xml
    sed -i -e "s/<HostName>localhost<\/HostName>/<HostName>$IP<\/HostName>/g" $AS_HOME1/repository/conf/carbon.xml
    sed -i -e "s/<HostName>localhost<\/HostName>/<HostName>$IP<\/HostName>/g" $AS_HOME2/repository/conf/carbon.xml
    sed -i -e "s/<MgtHostName>localhost<\/MgtHostName>/<MgtHostName>$IP<\/MgtHostName>/g" $AS_HOME1/repository/conf/carbon.xml
    sed -i -e "s/<MgtHostName>localhost<\/MgtHostName>/<MgtHostName>$IP<\/MgtHostName>/g" $AS_HOME2/repository/conf/carbon.xml

    sed -i -e "s/https:\/\/localhost:9443\/appmgt\/jagg\/jaggery_acs.jag/http:\/\/$IP\/appmgt\/jagg\/jaggery_acs.jag/g" $IS_HOME/repository/conf/security/sso-idp-config.xml

    cp $APP_CLOUD_SRC_HOME/modules/setup-scripts/conf/wso2as-5.2.1/repository/conf/axis2/axis2.xml $AS_HOME1/repository/conf/axis2/
    cp $APP_CLOUD_SRC_HOME/modules/setup-scripts/conf/wso2as-5.2.1/repository/conf/axis2/axis2.xml $AS_HOME2/repository/conf/axis2/

    echo "AS cluster setup successfully done!"

}

function as_non_cluster_setup(){

    unzip -q $PACK_DIR/$AS_VERSION.zip -d $SETUP_DIR
    AS_HOME=$SETUP_DIR/$AS_VERSION/

    as_setup $AS_HOME
    echo "AS non cluster setup successfully done!"
}


echo "Updaing IS node with new configuraitons"
cp $APP_CLOUD_SRC_HOME/modules/setup-scripts/lib/mysql-connector-java-5.1.27-bin.jar $IS_HOME/repository/components/lib/
cp $APP_CLOUD_SRC_HOME/modules/setup-scripts/conf/wso2is-5.0.0/repository/conf/datasources/master-datasources.xml $IS_HOME/repository/conf/datasources/
cp $APP_CLOUD_SRC_HOME/modules/setup-scripts/conf/wso2is-5.0.0/repository/conf/identity.xml $IS_HOME/repository/conf/
cp $APP_CLOUD_SRC_HOME/modules/setup-scripts/conf/wso2is-5.0.0/repository/conf/user-mgt.xml $IS_HOME/repository/conf/
cp $APP_CLOUD_SRC_HOME/modules/setup-scripts/conf/wso2is-5.0.0/repository/conf/carbon.xml $IS_HOME/repository/conf/
cp $APP_CLOUD_SRC_HOME/modules/setup-scripts/conf/wso2is-5.0.0/repository/conf/security/sso-idp-config.xml $IS_HOME/repository/conf/security/sso-idp-config.xml


echo "Updaing AS node with new configurations"
read -p "Do you wish to do a clustered setup?" yn
     case $yn in
         [Yy]* ) as_cluster_setup;;
         [Nn]* ) as_non_cluster_setup;;
         * ) echo "Please answer yes or no.";;
     esac


echo "Updating SS node with new configurations"
cp $APP_CLOUD_SRC_HOME/modules/setup-scripts/lib/mysql-connector-java-5.1.27-bin.jar $SS_HOME/repository/components/lib/
cp $APP_CLOUD_SRC_HOME/modules/setup-scripts/lib/nimbus-jose-jwt_2.26.1.wso2v2.jar $SS_HOME/repository/components/dropins/
cp $APP_CLOUD_SRC_HOME/modules/setup-scripts/lib/signedjwt-authenticator_4.3.3.jar $SS_HOME/repository/components/dropins/
cp $APP_CLOUD_SRC_HOME/modules/setup-scripts/conf/wso2ss-1.1.0/repository/conf/datasources/master-datasources.xml $SS_HOME/repository/conf/datasources/
cp $APP_CLOUD_SRC_HOME/modules/setup-scripts/conf/wso2ss-1.1.0/repository/conf/user-mgt.xml $SS_HOME/repository/conf/
cp $APP_CLOUD_SRC_HOME/modules/setup-scripts/conf/wso2ss-1.1.0/repository/conf/carbon.xml $SS_HOME/repository/conf/
cp $APP_CLOUD_SRC_HOME/modules/setup-scripts/conf/wso2ss-1.1.0/repository/conf/etc/* $SS_HOME/repository/conf/etc/
cp -r $APP_CLOUD_SRC_HOME/modules/setup-scripts/patches/wso2ss-1.1.0/* $SS_HOME/repository/components/patches/

echo "Updating DAS node with new configurations"
cp $APP_CLOUD_SRC_HOME/modules/setup-scripts/conf/wso2das-3.0.1/repository/conf/datasources/master-datasources.xml $DAS_HOME/repository/conf/datasources/
cp $APP_CLOUD_SRC_HOME/modules/setup-scripts/conf/wso2das-3.0.1/repository/conf/datasources/analytics-datasources.xml $DAS_HOME/repository/conf/datasources/
cp $APP_CLOUD_SRC_HOME/modules/setup-scripts/lib/mysql-connector-java-5.1.27-bin.jar $DAS_HOME/repository/components/lib/
mkdir -p $DAS_HOME/repository/deployment/server/carbonapps
cp $APP_CLOUD_SRC_HOME/modules/setup-scripts/conf/wso2das-3.0.1/repository/deployment/server/capps/*.car $DAS_HOME/repository/deployment/server/carbonapps
cp -r $APP_CLOUD_SRC_HOME/modules/setup-scripts/conf/wso2das-3.0.1/repository/deployment/server/jaggeryapps/monitoring $DAS_HOME/repository/deployment/server/jaggeryapps/
cp -r $APP_CLOUD_SRC_HOME/modules/setup-scripts/conf/wso2das-3.0.1/modules $DAS_HOME/
cp $APP_CLOUD_SRC_HOME/modules/setup-scripts/conf/wso2das-3.0.1/repository/conf/carbon.xml $DAS_HOME/repository/conf/


sh $IS_HOME/bin/wso2server.sh -Dsetup &
sleep 60

if [ $yn == "yes" ]
then
    sh $AS_HOME1/bin/wso2server.sh &
    sleep 60
    sh $AS_HOME2/bin/wso2server.sh &
    sleep 60
else
    sh $AS_HOME/bin/wso2server.sh &
    sleep 60
fi

sh $SS_HOME/bin/wso2server.sh -Dsetup &
sleep 60
sh $DAS_HOME/bin/wso2server.sh -Dsetup &

echo "If you are setting up App Cloud for the first time, please make sure to run app-cloud/modules/resources/dockerfiles/dockerImages.sh script to build docker base images"
echo "Set up is completed."

