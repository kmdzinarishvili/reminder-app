# Reminders App
*Ketevan Mdzinarishvili*

Spring Boot application to handle recurring reminders.

*[Relevant Jira Board](https://kmdzinarishvili.atlassian.net/jira/software/projects/SB/boards/1)*


## Requirements

For building and running the application you need:

- [JDK 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
- [Maven 3.9.2](https://maven.apache.org/docs/3.9.2/release-notes.html)
- [Docker](https://docs.docker.com/engine/install/)

## Running the application locally

Initially, you need to start the docker daemon. 
This can be done by starting Docker Desktop or running the command 
```shell
dockerd
```

For local development, you can start by opening the docker-compose file in your IDE and pressing run next to db
or you can run it using the following command:
```shell
docker-compose start db
```

The application can be run locally by executing the `main` method in the `com.lineate.mdzinarishvili.reminderapp.ReinderAppApplication` class from your IDE.

Alternatively you can use the [Spring Boot Maven plugin](https://docs.spring.io/spring-boot/docs/current/reference/html/build-tool-plugins-maven-plugin.html) like so:
```shell
mvn spring-boot:run
```

To run migrations, use the following command:
```shell
mvn flyway:migrate
```

## Production build
Run the following command to start the docker compose file. 
```shell
docker-compose start
```


