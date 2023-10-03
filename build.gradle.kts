val dgsProjects = setOf(
    "app-service-users",
    "app-service-posts",
    "app-service-comments",
)

tasks {
    register("dgsCodegen") {
        group = "build"
        description = "Generates all DGS related code"

        for (codegen in gradle.includedBuilds.filter { it.name in dgsProjects }.map { it.task(":generateJava") }) {
            dependsOn(codegen)
        }
    }
}
