dependencies {
    implementation(project(":core"))
    implementation(project(":infrastructure:message-broker"))

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
}
