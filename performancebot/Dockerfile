FROM maven:latest
VOLUME /tmp
ADD target/*.jar /app.jar
ADD target/classes/dummy.pem /
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]