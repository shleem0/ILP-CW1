FROM maven:3.8.5-openjdk-18 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:18-jre-alpine
WORKDIR /app
COPY --from=build /app/target/pizza-dronz.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]