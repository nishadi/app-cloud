How to run this sample
=========================

1. Change the following DB related parameters in DBUtil.java accordingly:
        String url          = "jdbc:mysql://localhost:3306/BuzzWordSampleDB";
        String dbUsername   = "root";
        String dbPassword   = "root";

2.  Create the table Buzzwords as below:

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

    