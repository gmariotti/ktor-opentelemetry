# OpenTelemetry with Ktor

This project is a replica of the [example javaagent project](https://github.com/open-telemetry/opentelemetry-java-docs/tree/main/javaagent) 
using Spring Boot in the OpenTelemetry repository.

## How to run

- Run the collector with `$ docker compose up`.
- Run the application with `$./gradlew run`. This command will take care of downloading the OpenTelemetry Java Agent.
- Send an example HTTP request with `$ http localhost:8080/ping`.