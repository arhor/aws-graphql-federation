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
includeBuild("../shared-lib")
includeBuild("../shared-security")

rootProject.name = "dgs-users"
