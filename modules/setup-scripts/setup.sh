#!/bin/bash
# This is a basic script to get a developer setup done with multi-tenancy and sso
# Update the pack_dir and setup_dir and run. it will drop existing databases and setup 
PACK_DIR=/home/manjula/appcloud/packs
SETUP_DIR=/home/manjula/appcloud/setup
AS_VERSION=wso2as-5.2.1
IS_VERSION=wso2is-5.0.0

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

# Unzip default wso2carbon product packs and configure
mkdir -p $SETUP_DIR
unzip -q $PACK_DIR/$AS_VERSION.zip -d $SETUP_DIR/
unzip -q $PACK_DIR/$IS_VERSION.zip -d $SETUP_DIR/

AS_HOME=$SETUP_DIR/$AS_VERSION/ 
IS_HOME=$SETUP_DIR/$IS_VERSION/

echo "Updaing AS node with new configurations"
cp -r $APP_CLOUD_SRC_HOME/modules/setup-scripts/jaggery/modules/* $AS_HOME/modules/
mkdir -p $AS_HOME/repository/deployment/server/jaggeryapps/appmgt/
unzip -q $APP_CLOUD_SRC_HOME/modules/jaggeryapps/appmgt/target/appmgt-1.0.0-SNAPSHOT.zip -d $AS_HOME/repository/deployment/server/jaggeryapps/appmgt/

cp $APP_CLOUD_SRC_HOME/modules/setup-scripts/lib/org.wso2.carbon.hostobjects.sso_4.2.0.jar $AS_HOME/repository/components/dropins/
cp $APP_CLOUD_SRC_HOME/modules/setup-scripts/lib/mysql-connector-java-5.1.27-bin.jar $AS_HOME/repository/components/lib/
cp $APP_CLOUD_SRC_HOME/modules/setup-scripts/lib/docker-java-2.1.4.jar $AS_HOME/repository/components/lib/
cp $APP_CLOUD_SRC_HOME/modules/setup-scripts/conf/wso2as-5.2.1/repository/conf/datasources/master-datasources.xml $AS_HOME/repository/conf/datasources/
cp $APP_CLOUD_SRC_HOME/modules/setup-scripts/conf/wso2as-5.2.1/repository/conf/datasources/appcloud-datasources.xml $AS_HOME/repository/conf/datasources/
sed -e "s@AS_HOME@${AS_HOME}@g" $APP_CLOUD_SRC_HOME/modules/setup-scripts/jaggery/site.json > $AS_HOME/repository/deployment/server/jaggeryapps/appmgt/site/conf/site.json
cp $APP_CLOUD_SRC_HOME/modules/components/org.wso2.appcloud.core/target/org.wso2.appcloud.core-1.0.0-SNAPSHOT.jar $AS_HOME/repository/components/dropins/
cp $APP_CLOUD_SRC_HOME/modules/components/org.wso2.appcloud.provisioning.runtime/target/org.wso2.appcloud.provisioning.runtime-1.0.0-SNAPSHOT.jar $AS_HOME/repository/components/dropins/
cp $APP_CLOUD_SRC_HOME/modules/components/org.wso2.appcloud.common/target/org.wso2.appcloud.common-1.0.0-SNAPSHOT.jar $AS_HOME/repository/components/dropins/
mkdir -p $AS_HOME/repository/conf/appcloud
cp $APP_CLOUD_SRC_HOME/modules/setup-scripts/conf/wso2as-5.2.1/repository/conf/appcloud/appcloud.properties $AS_HOME/repository/conf/appcloud/

echo "Updaing IS node with new configuraitons"
cp $APP_CLOUD_SRC_HOME/modules/setup-scripts/lib/mysql-connector-java-5.1.27-bin.jar $IS_HOME/repository/components/lib/
cp $APP_CLOUD_SRC_HOME/modules/setup-scripts/conf/wso2is-5.0.0/repository/conf/datasources/master-datasources.xml $IS_HOME/repository/conf/datasources/
cp $APP_CLOUD_SRC_HOME/modules/setup-scripts/conf/wso2is-5.0.0/repository/conf/identity.xml $IS_HOME/repository/conf/
cp $APP_CLOUD_SRC_HOME/modules/setup-scripts/conf/wso2is-5.0.0/repository/conf/user-mgt.xml $IS_HOME/repository/conf/
cp $APP_CLOUD_SRC_HOME/modules/setup-scripts/conf/wso2is-5.0.0/repository/conf/carbon.xml $IS_HOME/repository/conf/
cp $APP_CLOUD_SRC_HOME/modules/setup-scripts/conf/wso2is-5.0.0/repository/conf/security/sso-idp-config.xml $IS_HOME/repository/conf/security/sso-idp-config.xml

sh $IS_HOME/bin/wso2server.sh -Dsetup &
sleep 60
sh $AS_HOME/bin/wso2server.sh &
echo "Set up is completed."

