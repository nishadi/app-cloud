#!/bin/bash

echo "$(tput setaf 2)Enter the Domain name :$(tput sgr0)(e.g.:- registry.docker.appfactory.private.wso2.com:5000) "
read domain

echo "$(tput setaf 2)Enter the Host Port of the Remote Registry :$(tput sgr0)(e.g. :- 5000) "
read hostPort

sudo openssl s_client -showcerts -connect $domain:$hostPort </dev/null 2>/dev/null|openssl x509 -outform PEM >$domain.crt


sudo cp $domain.crt /usr/local/share/ca-certificates/$domain.crt
sudo cp $domain.crt /etc/ssl/certs/$domain.crt

# update certificcates
sudo update-ca-certificates

#update docker file
sudo sed -i '$ a DOCKER_OPTS="--insecure-registry '$domain:$hostPort'"' /etc/default/docker

#restart the docker service
sudo service docker stop
sudo service docker start

echo "$(tput setaf 2)Enter the IP of the Remote Host :"$(tput sgr0)"(e.g. :- 192.168.16.2) "
read remoteIP


# add host entry to domain
sudo sed -i "$ a $remoteIP $domain" /etc/hosts
echo "$(tput setaf 3)Updated the host entry$(tput sgr0)"

