pluginManagement {
    repositories {
        maven { url = uri("https://repo.spring.io/milestone") }
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../shared-bom/gradle/libs.versions.toml"))
        }
    }
}

includeBuild("../shared-bom")

rootProject.name = "shared-security"
