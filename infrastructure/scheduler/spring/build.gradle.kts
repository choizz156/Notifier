dependencies {
    implementation(project(":core"))
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("net.javacrumbs.shedlock:shedlock-spring:5.13.0")
    implementation("net.javacrumbs.shedlock:shedlock-provider-jdbc-template:5.13.0")
}
