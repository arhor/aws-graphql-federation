dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../shared-bom/gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "dgs-posts"

includeBuild("../shared-bom")
includeBuild("../shared-lib")
