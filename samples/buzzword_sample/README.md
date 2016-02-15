How to run this sample
=========================

1. Create the Database related attributes and API Management related attributes with the below Environment Variable Names:
        String jdbcUrl      = DB_URL
        String dbUsername   = DB_USERNAME;
        String dbPassword   = DB_PASSWORD;

        String apiManagerUrl    = API_MANAGER_URL
        String apiEndpointUrl   = API_ENDPOINT_URL
        String consumerKey      = API_CONSUMER_KEY
        String consumerSecret   = API_CONSUMER_SECRET
        String username         = TENANT_USERNAME
        String password         = TENANT_PASSWORD

    Example values for the above Environment variables:
        DB_URL              = jdbc:mysql://localhost:3306/BuzzWordSampleDB
        DB_USERNAME         = root
        DB_PASSWORD         = root

        API_MANAGER_URL     = http://172.17.0.1:9443
        API_ENDPOINT_URL    = http://172.17.0.1:8280/buzzword/1.0.0/all
        API_CONSUMER_KEY    = YaBFwIgxfEPDzicCgkC8zlrcyHsa
        API_CONSUMER_SECRET = QuJ2dEvTwdLe3q0ciXtD3ECu0O4a
        TENANT_USERNAME     = admin
        TENANT_PASSWORD     = admin


2.  Create the table "Buzzwords" as below:
    CREATE TABLE Buzzwords (ID INT NOT NULL AUTO_INCREMENT, Word VARCHAR(255) NOT NULL, Popularity	INT, PRIMARY KEY (ID));
    +------------+--------------+------+-----+---------+----------------+
    | Field      | Type         | Null | Key | Default | Extra          |
    +------------+--------------+------+-----+---------+----------------+
    | ID         | int(11)      | NO   | PRI | NULL    | auto_increment |
    | Word       | varchar(255) | NO   |     | NULL    |                |
    | Popularity | int(11)      | YES  |     | NULL    |                |
    +------------+--------------+------+-----+---------+----------------+

3. Build the pom using "mvn clean install"

4. Go to the target directory and run the command below:
        java -jar Buzzword-Service-1.0-SNAPSHOT.jar
    This will start up a Microservice related to the Buzzwords service.

5. Run the curl commands below:
    To insert the buzzwords:                            curl --data "Java" http://localhost:8080/buzzword
    To get buzzwords similar or equal to a given word:  curl -v http://localhost:8080/buzzword/Eclipse
    To get all the buzzwords:                           curl -v http://localhost:8080/buzzword/all
    To get the  mostpopular 10 buzzwords                curl -v http://localhost:8080/buzzword/mostPopular



