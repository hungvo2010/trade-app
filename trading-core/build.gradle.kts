plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation("org.springframework.boot:spring-boot-starter-web:4.0.0")
    implementation(project(":trading-base"))

    implementation("org.springframework.boot:spring-boot-starter-data-jpa:4.0.0")
    implementation("jakarta.persistence:jakarta.persistence-api:3.2.0")
}

tasks.test {
    useJUnitPlatform()
}