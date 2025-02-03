dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../app-library-platform/gradle/libs.versions.toml"))
        }
    }
}

includeBuild("../app-library-platform")

rootProject.name = "app-library-starter-security"
