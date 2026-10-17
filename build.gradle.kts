plugins {
    kotlin("jvm") version "2.4.10"
    kotlin("plugin.spring") version "2.4.10"
    id("org.springframework.boot") version "4.1.1"
    id("io.spring.dependency-management") version "1.1.7"
    id("com.google.protobuf") version "0.9.6"
    id("com.netflix.dgs.codegen") version "8.6.0"
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
    implementation("org.springframework.ai:spring-ai-starter-vector-store-pgvector")
    implementation("org.springframework.ai:spring-ai-rag")
    implementation("org.springframework.ai:spring-ai-starter-model-openai")

    // API
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-graphql")
    implementation("org.springframework.boot:spring-boot-starter-grpc-client")

    // DB
    implementation("org.springframework.boot:spring-boot-starter-liquibase")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // Observability
    implementation("io.github.oshai:kotlin-logging-jvm:8.0.4")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-opentelemetry")
    implementation("io.micrometer:micrometer-registry-prometheus")
    implementation("com.github.loki4j:loki-logback-appender:2.1.0")

    // Util
    implementation("com.netflix.graphql.dgs:graphql-dgs-extended-scalars")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("tools.jackson.module:jackson-module-kotlin")

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

tasks.withType<com.netflix.graphql.dgs.codegen.gradle.GenerateJavaTask> {
    schemaPaths = mutableListOf("$projectDir/src/main/resources/graphql")
    packageName = "ru.leti.graphql"
    generateClient = true
}

protobuf {
    plugins {
        create("grpc")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}