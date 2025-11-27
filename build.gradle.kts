plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-quartz:4.0.0")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.springframework.boot:spring-boot-starter-test:4.0.0")

    implementation("org.springframework.boot:spring-boot-starter-web:4.0.0")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:4.0.0")
    implementation("jakarta.persistence:jakarta.persistence-api:3.2.0")
    implementation("org.springframework.boot:spring-boot-starter-log4j2:4.0.0")
    runtimeOnly("com.h2database:h2:2.4.240")
    runtimeOnly("org.apache.logging.log4j:log4j-spring-boot:2.25.2")

    implementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.21.0")
    implementation("org.apache.logging.log4j:log4j-core:2.21.0")
    implementation("org.apache.logging.log4j:log4j-api:2.21.0")

}

tasks.test {
    useJUnitPlatform()
}