pluginManagement {
    val springBootVersion: String by settings
    val springDependencyManagementVersion: String by settings

    plugins {
        id("org.springframework.boot") version springBootVersion
        id("io.spring.dependency-management") version springDependencyManagementVersion
    }
}

rootProject.name = "Notifier"

include("api", "core", "application", "infrastructure:persistence", "infrastructure:persistence:jpa", "infrastructure:notifier", "support:logging")