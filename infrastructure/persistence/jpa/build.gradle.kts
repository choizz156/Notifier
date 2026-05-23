dependencies {
    project(":support:logging")
    implementation(project(":core"))

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    runtimeOnly("com.h2database:h2")
}
