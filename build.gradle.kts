val dgsProjects = setOf(
    "app-service-users",
    "app-service-posts",
    "app-service-comments",
)

tasks {
    val dgsCodegen by registering {
        group = "build"
        description = "Generates all DGS related code"

        gradle.includedBuilds.filter { it.name in dgsProjects }.map { it.task(":generateJava") }.forEach {
            dependsOn(it)
        }
    }

    val fullClean by registering {
        group = "build"
        description = "Cleans all included projects"

        gradle.includedBuilds.map { it.task(":clean") }.forEach {
            dependsOn(it)
        }
    }

    val fullBuild by registering {
        group = "build"
        description = "Builds all included projects"

        gradle.includedBuilds.map { it.task(":build") }.forEach {
            dependsOn(it)
        }
    }
}
