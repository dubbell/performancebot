FROM maven:3.9.0
VOLUME /tmp
ADD target/*.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]