#!/bin/bash

mkdir -p certs 

# get certificate 
echo "$(tput setaf 1)Common Name for the certificate should be the Host name$(tput sgr0)";
openssl req -newkey rsa:4096 -nodes -sha256 -keyout certs/domain.key -x509 -days 365 -out certs/domain.crt
echo 'Successfully created a certificate'

echo 'Enter the port that you want to map the container port'
read port

echo 'Enter the port that you want to map the host port'
read host

echo 'Enter the name for the Registry'
read name

#run the docker registry with TLS enabled
sudo docker run -d -p $host:$port --restart=always --name $name -v `pwd`/certs:/certs -e REGISTRY_HTTP_TLS_CERTIFICATE=/certs/domain.crt -e REGISTRY_HTTP_TLS_KEY=/certs/domain.key registry:2


