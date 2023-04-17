FROM maven:3.9.0
VOLUME /tmp
ADD target/*.jar /app.jar
ADD target/classes/dummy.pem /
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]