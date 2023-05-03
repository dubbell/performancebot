FROM maven:3-eclipse-temurin-17
VOLUME /tmp
ADD target/*.jar /app.jar
ADD target/classes/dummy.pem /
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]