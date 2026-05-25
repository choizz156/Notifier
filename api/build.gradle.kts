dependencies {
    implementation(project(":support:logging"))
    implementation(project(":core"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    testImplementation("org.springframework.boot:spring-boot-webmvc-test")
}
