To start with your local setup, you need to add below jars in current directory.

├── lib
│   ├── docker-java-2.1.4.jar
│   ├── mysql-connector-java-5.1.27-bin.jar
│   ├── nimbus-jose-jwt_2.26.1.wso2v2.jar
│   ├── org.wso2.carbon.hostobjects.sso_4.2.0.jar
│   └── signedjwt-authenticator_4.3.3.jar
├── patches
│   └── wso2ss-1.1.0
│       ├── patch0298
│       │   ├── org.wso2.carbon.rssmanager.common-4.2.0.jar
│       │   ├── org.wso2.carbon.rssmanager.core-4.2.0.jar
│       │   └── org.wso2.carbon.rssmanager.ui-4.2.0.jar
│       ├── patch0351
│       │   ├── org.wso2.carbon.rssmanager.core_4.2.0.jar
│       │   └── org.wso2.carbon.rssmanager.ui_4.2.0.jar
│       └── patch1085
│           └── org.wso2.carbon.rssmanager.core_4.2.0.jar


* If you are using DAS for the Dashboard, you need to install SSO module 1.4.4 from the feature repo http://product-dist.wso2.com/p2/carbon/releases/wilkes to DAS.

* To setup the cluster, first install the HAProxy load balancer as described in the guide [1]. Following is the sample configuration for this HAProxy.

frontend http-in
        bind *:80
        default_backend bk_http

backend bk_http
        balance roundrobin
        server node1 localhost:9763
        server node2 localhost:9767


frontend https-in
        bind *:443 ssl crt /etc/haproxy/ssl/server.pem
        default_backend bk_https

backend bk_https
        balance roundrobin
        server node1 localhost:9443 check ssl verify none
        server node2 localhost:9447 check ssl verify none


1. https://docs.wso2.com/display/CLUSTER420/Configuring+HAProxy

