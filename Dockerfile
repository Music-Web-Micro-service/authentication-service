FROM openjdk:17
WORKDIR /app
COPY . .
RUN mkdir -p /root/.m2 \
    && mkdir /root/.m2/repository
COPY ./.mvn/settings.xml /root/.m2/settings.xml
RUN chmod +x mvnw

ENV GITHUB_ACTOR=${GITHUB_ACTOR}
ENV GITHUB_TOKEN=${GITHUB_TOKEN}

RUN cat /root/.m2/settings.xml
#val
RUN ./mvnw clean package -f /root/.m2/settings.xml

# Expose the port that your Eureka server listens on
EXPOSE 8084

# Command to run your Spring Boot application
CMD ["java", "-jar", "./target/Authentication-service-0.0.1-SNAPSHOT.jar"]