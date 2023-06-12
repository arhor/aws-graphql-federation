import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.adarshr.test-logger")
    id("com.netflix.dgs.codegen")
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlin.kapt")
    id("org.jetbrains.kotlin.plugin.spring")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

val javaVersion = project.property("app.version.java")!!.toString()

java {
    javaVersion.let(JavaVersion::toVersion).let {
        sourceCompatibility = it
        targetCompatibility = it
    }
}

repositories {
    mavenCentral()
}

configurations {
    compileOnly {
        extendsFrom(annotationProcessor.get())
    }
    testImplementation {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
        exclude(group = "org.mockito", module = "mockito-core")
        exclude(group = "org.mockito", module = "mockito-junit-jupiter")
    }
    all {
        exclude(group = "org.apache.logging.log4j", module = "log4j-to-slf4j")
        exclude(group = "org.apache.logging.log4j", module = "log4j-api")
    }
}

dependencies {
    kapt(platform(":shared-bom"))
    kapt("org.mapstruct:mapstruct-processor")
    kapt("org.springframework:spring-context-indexer")
    kapt("org.springframework.boot:spring-boot-configuration-processor")

    compileOnly("com.google.code.findbugs:jsr305")
    compileOnly("org.mapstruct:mapstruct")

    developmentOnly("org.springframework.boot:spring-boot-devtools")

    implementation(platform(":shared-bom"))
    implementation(":shared-lib")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.github.ben-manes.caffeine:caffeine")
    implementation("com.netflix.graphql.dgs:graphql-dgs-extended-scalars")
    implementation("com.netflix.graphql.dgs:graphql-dgs-spring-boot-starter")
    implementation("org.flywaydb:flyway-core")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.postgresql:postgresql")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-web")

    testImplementation("com.ninja-squad:springmockk")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
}

dependencyManagement {
    imports {
        mavenBom("org.testcontainers:testcontainers-bom:${property("app.version.testcontainers")}")
        mavenBom("com.netflix.graphql.dgs:graphql-dgs-platform-dependencies:${property("app.version.graphql-dgs-bom")}")
    }
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = javaVersion
            javaParameters = true
            freeCompilerArgs = listOf(
                "-Xjsr305=strict",
                "-Xjvm-default=all",
                "-Xcontext-receivers",
            )
        }
    }

    withType<JavaCompile> {
        options.compilerArgs = listOf(
            "-Xlint:unchecked",
            "-Xlint:deprecation",
            "-Xlint:preview",
            "-parameters"
        )
    }

    generateJava {
        language = "kotlin"
        packageName = "com.github.arhor.dgs.extradata.generated.graphql"
        typeMapping = mutableMapOf(
            "Object" to "Map<String, Any>",
        )
    }

    test {
        useJUnitPlatform()
    }

    wrapper {
        gradleVersion = project.property("app.version.gradle").toString()
    }
}
