plugins {
    java
    id("org.springframework.boot") version "3.0.6"
    id("io.spring.dependency-management") version "1.1.0"
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(17))

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web:3.1.5")
    implementation("org.springframework.boot:spring-boot-starter-mail:3.1.5")

    implementation("org.springframework.boot:spring-boot-starter-security:3.1.5")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server:3.1.5")

    implementation("com.github.0xshamil:java-xid:1.0.0")
    implementation("com.google.guava:guava:30.0-jre")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-hibernate5-jakarta:2.14.2")

    implementation("org.springframework.boot:spring-boot-starter-data-jdbc:3.1.5")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.1.5")
    runtimeOnly("org.mariadb.jdbc:mariadb-java-client:3.2.0")

    annotationProcessor("org.projectlombok:lombok:1.18.30")
    compileOnly("org.projectlombok:lombok:1.18.30")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:3.1.5")
    developmentOnly("org.springframework.boot:spring-boot-devtools:3.1.5")

    testImplementation("org.springframework.boot:spring-boot-starter-test:3.1.5")
    testImplementation("org.springframework.security:spring-security-test:6.1.5")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
