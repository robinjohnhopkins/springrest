# comment line 
FROM openjdk:latest
#FROM openjdk:8
COPY target/springrest-0.0.1-SNAPSHOT.jar /usr/src/springrest-0.0.1-SNAPSHOT.jar

# at one point I saw in the logs
# Registered driver with driverClassName=com.mysql.jdbc.Driver was not found, trying direct instantiation
# If you need to add a file to the docker image, COPY is path relative e.g.
# However, it turned out this WARNING was a red herring and the code connected to mysql without :)
#
#COPY kubernetes/mysql-connector-java-8.0.15.jar  /usr/src/mysql-connector-java-8.0.15.jar

CMD java -jar /usr/src/springrest-0.0.1-SNAPSHOT.jar theredredrobin.com.springrest.SpringrestApplication

# docker build -t  robinjohnhopkins/springrest:v1 .
# docker run -it --rm --name SpringRest springrest:v1
