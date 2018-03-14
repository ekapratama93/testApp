FROM wrlennon/alpine-java8-jre:latest
WORKDIR /app
COPY target/test-0.0.1-SNAPSHOT.jar test.jar
EXPOSE 8080
CMD java -jar test.jar