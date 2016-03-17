#!/bin/sh
# This script build initial base docker images to be used in app cloud setup.
CURRENT_DIR=`pwd`

#msf4j base image
docker build -t wso2-appcloud/msf4j:1.0.0-base -f $CURRENT_DIR/msf4j/base/1.0.0/Dockerfile.wso2-appcloud-msf4j-1.0.0.base $CURRENT_DIR/msf4j/base/1.0.0/

#php base image
docker build -t wso2-appcloud/php:1.0.0-base -f $CURRENT_DIR/php/base/1.0.0/Dockerfile.wso2-appcloud-php-1.0.0.base $CURRENT_DIR/php/base/1.0.0

#tomcat base image
docker pull dell/tomcat:latest 
