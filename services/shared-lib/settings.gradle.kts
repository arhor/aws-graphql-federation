dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../shared-bom/gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "shared-lib"

includeBuild("../shared-bom")
