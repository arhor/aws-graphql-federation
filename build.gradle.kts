plugins {
    idea
}

val dgsProjects = setOf(
    "app-service-users",
    "app-service-posts",
    "app-service-comments",
)

idea {
    module {
        excludeDirs.addAll(
            files(
                "$projectDir/app-client-web/node_modules",
                "$projectDir/app-gateway/node_modules",
            )
        )
    }
}

tasks {
    val generateGql by registering {
        group = "build"
        description = "Generates all Graphql related code"

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

        val name = when (project.hasProperty("skip-test")) {
            true -> ":assemble"
            else -> ":build"
        }
        gradle.includedBuilds.map { it.task(name) }.forEach {
            dependsOn(it)
        }
    }
}
