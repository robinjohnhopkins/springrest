# comment line 
FROM openjdk:latest
#FROM openjdk:8
COPY target/springrest-0.0.1-SNAPSHOT.jar /usr/src/springrest-0.0.1-SNAPSHOT.jar

CMD java -jar /usr/src/springrest-0.0.1-SNAPSHOT.jar theredredrobin.com.springrest.SpringrestApplication

#    image: openjdk:9-jdk-slim  alternate image
#
# docker build -t  springrest:v1 .
# docker run -it --rm --name SpringRest springrest:v1
