
#
# Build stage
#
FROM maven:3.9.1-eclipse-temurin-17-alpine AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package


# Package stage

FROM eclipse-temurin:17-jdk-alpine
COPY --from=build /home/app/target/backend-1.0-SNAPSHOT.jar /usr/local/lib/backend.jar
EXPOSE 8000
ENTRYPOINT ["java","-jar","/usr/local/lib/backend.jar"]