import org.springframework.boot.gradle.tasks.bundling.BootJar

tasks.named<BootJar>("bootJar") {
    enabled = true
    mainClass.set("io.github.choizz.notifier.NotifierApplication")
}

tasks.named<Jar>("jar") {
    enabled = false
}

dependencies {
    implementation(project(":api"))
    implementation(project(":admin"))
    implementation(project(":core"))
    implementation(project(":infrastructure:persistence:jpa"))
    implementation(project(":infrastructure:notifier:email"))
    implementation(project(":infrastructure:notifier:in_app"))
    implementation(project(":infrastructure:message-broker:rdb"))
    implementation(project(":infrastructure:scheduler:spring"))
    
    implementation("org.springframework.boot:spring-boot-starter-web")
}
