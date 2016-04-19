#!/bin/bash
SERVICE_URL=https://10.100.7.115:9443
ADMIN_USERNAME="admin"
ADMIN_PASSWORD="admin"
NUMBER_OF_HOURS=2
 
curl -c cookies -v -X POST -k $SERVICE_URL/appmgt/site/blocks/user/login/ajax/login.jag -d "action=login&userName=$ADMIN_USERNAME&password=$ADMIN_PASSWORD"

curl -b cookies  -v -X POST -k $SERVICE_URL/appmgt/site/blocks/admin/admin.jag -d "action=stopIdleApplications&numberOfHours=$NUMBER_OF_IDLE_DAYS"
