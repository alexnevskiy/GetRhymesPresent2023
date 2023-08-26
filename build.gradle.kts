import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    java
    id("org.springframework.boot") version "3.1.2"
    id("io.spring.dependency-management") version "1.1.2"
    id("io.freefair.lombok") version "8.2.1"
}

apply(plugin = "io.spring.dependency-management")

group = "com.poly"
version = "1.0.0"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.telegram:telegrambots:6.7.0")
    implementation("org.slf4j:slf4j-api:2.0.7")
    implementation("ch.qos.logback:logback-core:1.4.11")
    implementation("org.xerial:sqlite-jdbc:3.42.0.0")
    implementation("org.hibernate.orm:hibernate-community-dialects:6.3.0.CR1")
    compileOnly("org.projectlombok:lombok:1.18.28")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

springBoot {
    mainClass.set("com.poly.getrhymespresent2023.Application")
}

tasks.named<BootJar>("bootJar") {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    manifest {
        attributes("Start-Class" to "com.poly.getrhymespresent2023.Application")
    }
}
