plugins {
    idea
}

idea {
    module {
        listOf("app-client-web", "app-gateway").forEach {
            excludeDirs.add(
                file("$projectDir/$it/node_modules")
            )
        }
    }
}

tasks {
    val generateGql by registering {
        group = "build"
        description = "Generates all Graphql related code"

        listOf("app-service-users", "app-service-posts", "app-service-comments").forEach {
            dependsOn(gradle.includedBuild(it).task(":generateJava"))
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
