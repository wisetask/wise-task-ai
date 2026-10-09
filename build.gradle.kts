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
    implementation("org.springframework.ai:spring-ai-starter-mcp-server")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.ai:spring-ai-starter-model-deepseek")
    implementation("org.springframework.boot:spring-boot-starter-grpc-client")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("io.github.oshai:kotlin-logging-jvm:8.0.4")
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
        create("grpc")   // ← вот это и есть opt-in
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}