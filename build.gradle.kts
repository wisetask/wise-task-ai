plugins {
    kotlin("jvm") version "2.4.10"
    id("org.springframework.boot") version "4.1.1"
    id("io.spring.dependency-management") version "1.1.7"
    id("com.google.protobuf") version "0.9.6"
}

group = "ru.leti.wisetask.ai"
version = "1.0.0-SNAPSHOT"


java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}
extra["springAiVersion"] = "2.0.1"

repositories {
    mavenCentral()
}

dependencies {
    // AI
    implementation("org.springframework.ai:spring-ai-starter-mcp-server")
    implementation("org.springframework.ai:spring-ai-starter-model-deepseek")

    // API
    implementation("org.springframework.boot:spring-boot-starter-graphql")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("org.springframework.boot:spring-boot-starter-grpc-client")

    // DB
    implementation("org.springframework.boot:spring-boot-starter-liquibase")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // Observability
    implementation("io.github.oshai:kotlin-logging-jvm:8.0.4")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-opentelemetry")
    implementation ("io.micrometer:micrometer-registry-prometheus")
    implementation ("com.github.loki4j:loki-logback-appender:2.1.0")

    // Util
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation(kotlin("test"))
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.ai:spring-ai-bom:${property("springAiVersion")}")
    }
}
sourceSets {
    main {
        proto {
            srcDir("src/main/resources/proto")
        }
    }
}
kotlin {
    jvmToolchain(25)
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
    }
}

protobuf {
    plugins {
        create("grpc")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}