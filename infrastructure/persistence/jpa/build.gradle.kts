dependencies {
    project(":support:logging")
    implementation(project(":core"))

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("com.h2database:h2")
}
