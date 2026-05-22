import org.springframework.boot.gradle.tasks.bundling.BootJar

tasks.named<BootJar>("bootJar") {
    enabled = true
    mainClass.set("io.github.choizz.notifier.NotifierApplication")
}

tasks.named<Jar>("jar") {
    enabled = false
}

dependencies {
    implementation(project(":support:logging"))
    implementation(project(":infrastructure:persistence:jpa"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}