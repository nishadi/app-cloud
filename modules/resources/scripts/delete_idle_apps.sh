#!/bin/bash
SERVICE_URL=https://10.100.7.115:9443
ADMIN_USERNAME="admin"
ADMIN_PASSWORD="admin"
NUMBER_OF_HOURS=2
LOGFILE=delete_idle_apps.log

echo "----------Login to admin service----------" >> $LOGFILE
curl -c cookies -v -X POST -k $SERVICE_URL/appmgt/site/blocks/user/login/ajax/login.jag -d "action=login&userName=$ADMIN_USERNAME&password=$ADMIN_PASSWORD" >> $LOGFILE 2>&1
echo -e "\n" >> $LOGFILE
echo "----------Delete all idle applications----------" >> $LOGFILE
curl -b cookies  -v -X POST -k $SERVICE_URL/appmgt/site/blocks/admin/admin.jag -d "action=stopIdleApplications&numberOfHours=$NUMBER_OF_HOURS" >> $LOGFILE 2>&1
echo -e "\n" >> $LOGFILE