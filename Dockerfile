FROM maven:3.8.5-openjdk-18 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:18-jre-alpine
WORKDIR /app
COPY --from=build *.jar .
CMD ["java", "-jar", "pizza-dronz.jar"]