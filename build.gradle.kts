import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm") version "1.7.22"
}

repositories {
    mavenCentral()
}

val opentelemetry by configurations.creating
dependencies {
    implementation(platform("io.ktor:ktor-bom:2.1.1"))
    implementation("io.ktor:ktor-serialization-jackson")
    implementation("io.ktor:ktor-server-content-negotiation")
    implementation("io.ktor:ktor-server-core")
    implementation("io.ktor:ktor-server-status-pages")
    implementation("io.ktor:ktor-server-netty")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8")

    implementation("ch.qos.logback:logback-classic:1.4.5")

    implementation(platform("io.opentelemetry:opentelemetry-bom:1.20.1"))
    implementation("io.opentelemetry:opentelemetry-api")
    implementation("io.opentelemetry:opentelemetry-extension-kotlin")
    implementation("io.opentelemetry.instrumentation:opentelemetry-ktor-2.0:1.20.2-alpha")
    implementation("io.opentelemetry.instrumentation:opentelemetry-logback-appender-1.0:1.20.2-alpha")
    opentelemetry("io.opentelemetry.javaagent:opentelemetry-javaagent:1.20.2")
}

application {
    mainClass.set("ApplicationKt")
    applicationDefaultJvmArgs = listOf(
        "-javaagent:$buildDir/otel/otel-javaagent.jar",
        "-Dotel.service.name=ktor-opentelemetry",
        "-Dotel.exporter.otlp.endpoint=http://localhost:4317",
        "-Dotel.logs.exporter=otlp"
    )
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "17"
            apiVersion = "1.7"
            languageVersion = "1.7"
        }
    }
    val otelAgent = register<Copy>("otel-agent") {
        from(opentelemetry) {
            rename { "otel-javaagent.jar" }
        }
        into(file("$buildDir/otel/"))
    }
    getByName("run").dependsOn(otelAgent)
}