pluginManagement {
    val springBootVersion: String by settings
    val springDependencyManagementVersion: String by settings

    plugins {
        id("org.springframework.boot") version springBootVersion
        id("io.spring.dependency-management") version springDependencyManagementVersion
    }
}

rootProject.name = "Notifier"

include(
    "app",
    "api",
    "core",

    "infrastructure:persistence",
    "infrastructure:persistence:jpa",
    "infrastructure:notifier",
    "infrastructure:notifier:email",
    "infrastructure:notifier:in_app",
    "infrastructure:message-broker",
    "infrastructure:message-broker:rdb",
    "infrastructure:scheduler",
    "infrastructure:scheduler:spring",

    "support:logging"
)