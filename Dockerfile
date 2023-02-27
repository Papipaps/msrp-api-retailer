FROM maven:3.8.2-jdk-11 AS build
COPY src /app/src/
COPY pom.xml /app/
WORKDIR /app
RUN mvn clean package -DskipTests

#
# Package stage
#
FROM openjdk:11
EXPOSE 8081
COPY --from=build /app/target/*.jar /usr/app/app.jar
ENTRYPOINT ["java","-jar","/usr/app/app.jar"]