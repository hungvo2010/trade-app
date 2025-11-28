plugins {
    id("java")
    id("java-library")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    api("org.springframework.boot:spring-boot-jackson:4.0.0")
    api("tools.jackson.core:jackson-databind:3.0.0")
    api("com.fasterxml.jackson.core:jackson-annotations:2.20")

    api("org.springframework.boot:spring-boot-starter-data-jpa:4.0.0")
    api("jakarta.persistence:jakarta.persistence-api:3.2.0")

    api("org.projectlombok:lombok:1.18.42")
    annotationProcessor("org.projectlombok:lombok:1.18.42")

    api("org.mapstruct:mapstruct:1.6.3")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.6.3")
}

tasks.test {
    useJUnitPlatform()
}