FROM openjdk:8
VOLUME c:/temp
EXPOSE 8080
ADD ./target/ms-cliente-rest-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]