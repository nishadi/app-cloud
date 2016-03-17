#!/bin/sh
# ------------------------------------------------------------------------
#
# Copyright 2005-2016 WSO2, Inc. (http://wso2.com)
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License
#
# ------------------------------------------------------------------------

# This script build initial base docker images to be used in app cloud setup.
CURRENT_DIR=`pwd`

#msf4j base image
docker build -t wso2-appcloud/msf4j:1.0.0-base -f $CURRENT_DIR/msf4j/base/1.0.0/Dockerfile.wso2-appcloud-msf4j-1.0.0.base $CURRENT_DIR/msf4j/base/1.0.0/

#php base image
docker build -t wso2-appcloud/php:1.0.0-base -f $CURRENT_DIR/php/base/1.0.0/Dockerfile.wso2-appcloud-php-1.0.0.base $CURRENT_DIR/php/base/1.0.0

#tomcat base image
docker pull dell/tomcat:latest 
