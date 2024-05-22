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
        description = "Cleanes all included projects"

        gradle.includedBuilds.map { it.task(":clean") }.forEach {
            dependsOn(it)
        }
    }
}
