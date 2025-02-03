dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../app-library-platform/gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "app-library-common"

includeBuild("../app-library-platform")
