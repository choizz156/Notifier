dependencies {
    implementation(project(":core"))
    implementation(project(":infrastructure:message-broker"))

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.retry:spring-retry:2.0.5")
}
