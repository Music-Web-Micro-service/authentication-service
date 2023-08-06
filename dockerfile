FROM openjdk:17
WORKDIR /app

COPY ./target/authentication-service-0.0.1-SNAPSHOT.jar ./target/authentication-service-0.0.1-SNAPSHOT.jar

EXPOSE 8084

CMD ["java", "-jar", "./target/authentication-service-0.0.1-SNAPSHOT.jar"]