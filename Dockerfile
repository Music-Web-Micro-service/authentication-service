FROM openjdk:17
WORKDIR /app

COPY ./target/Authentication-service-0.0.1-SNAPSHOT.jar ./target/Authentication-service-0.0.1-SNAPSHOT.jar

EXPOSE 8084

# Command to run your Spring Boot application
CMD ["java", "-jar", "./target/Authentication-service-0.0.1-SNAPSHOT.jar"]