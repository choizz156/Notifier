import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("java")
    id("org.springframework.boot") apply false
    id("io.spring.dependency-management") apply false
}

val projectGroup: String by project
val projectVersion: String by project
val javaVersion: String by project


allprojects {
    group = projectGroup
    version = projectVersion

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")

    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(21)
        }
    }

    dependencies{
        compileOnly("org.projectlombok:lombok")
        testCompileOnly("org.projectlombok:lombok")
        testAnnotationProcessor("org.projectlombok:lombok")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")
        annotationProcessor("org.projectlombok:lombok")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    tasks.named<BootJar>("bootJar") {
        enabled = false
        mainClass.set("dummy")
    }
    tasks.named<Jar>("jar") {
        enabled = true
    }
}

