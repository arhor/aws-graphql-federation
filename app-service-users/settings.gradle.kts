dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../app-library-platform/gradle/libs.versions.toml"))
        }
    }
}

includeBuild("../app-library-platform")
includeBuild("../app-library-common")
includeBuild("../app-library-starter-core")
includeBuild("../app-library-starter-graphql")
includeBuild("../app-library-starter-security")
includeBuild("../app-library-starter-testing")
includeBuild("../app-library-starter-tracing")

rootProject.name = "app-service-users"
