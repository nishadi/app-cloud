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

