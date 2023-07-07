FROM maven:3.8.4-openjdk-17 as build
WORKDIR /app

COPY pom.xml .

RUN mvn dependency:resolve

COPY src ./src

RUN mvn package -DskipTests

FROM openjdk:17

WORKDIR /app

COPY --from=build /app/target/reminder-app-0.0.1-SNAPSHOT.jar .

EXPOSE 8080

CMD ["java", "-jar", "reminder-app.0.0.1-SNAPSHOT.jar"]
