dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../shared-bom/gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "dgs-articles"

includeBuild("../shared-bom")
includeBuild("../shared-lib")
