FROM openjdk:17
WORKDIR /app
COPY . .
RUN mkdir -p /root/.m2 \
    && mkdir /root/.m2/repository
COPY ./.mvn/settings.xml /root/.m2
RUN chmod +x mvnw
RUN ./mvnw clean package

# Expose the port that your Eureka server listens on
EXPOSE 8084

# Command to run your Spring Boot application
CMD ["java", "-jar", "./target/Authentication-service-0.0.1-SNAPSHOT.jar"]