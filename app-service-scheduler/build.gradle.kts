import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.test.logger)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.plugin.spring)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.deps)
    jacoco
}

extra["kotlin.version"] = libs.versions.kotlin.get()
val javaVersion: String = libs.versions.java.get()

java {
    javaVersion.let(JavaVersion::toVersion).let {
        sourceCompatibility = it
        targetCompatibility = it
    }
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(javaVersion))
    }
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(javaVersion))
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
    implementation(platform(":app-library-platform"))
    implementation(":app-library-common")
    implementation(":app-library-starter-core")
    implementation(":app-library-starter-tracing")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.awspring.cloud:spring-cloud-aws-starter-sns")
    implementation("io.awspring.cloud:spring-cloud-aws-starter-sqs")
    implementation("io.micrometer:micrometer-tracing-bridge-brave")
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.retry:spring-retry")

    runtimeOnly("org.postgresql:postgresql")

    developmentOnly("org.springframework.boot:spring-boot-devtools")

    testImplementation(platform(":app-library-platform"))
    testImplementation("com.ninja-squad:springmockk")
    testImplementation("com.tngtech.archunit:archunit-junit5")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

dependencyManagement {
    imports {
        mavenBom(libs.bom.spring.cloud.aws.get().toString())
    }
}

tasks {
    withType<KotlinCompile> {
        compilerOptions {
            jvmTarget.set(JvmTarget.fromTarget(javaVersion))
            javaParameters.set(true)
            freeCompilerArgs.set(
                listOf(
                    "-Xjsr305=strict",
                    "-Xjvm-default=all",
                    "-Xcontext-receivers",
                )
            )
        }
    }

    withType<Test> {
        jvmArgs = listOf("-XX:+EnableDynamicAgentLoading")
        systemProperty("spring.profiles.active", "test")
        useJUnitPlatform()
    }

    buildNeeded {
        gradle.includedBuilds.map { it.task(":build") }.forEach {
            dependsOn(it)
        }
    }

    processResources {
        filesMatching("application.yml") {
            expand(project.properties)
        }
    }

    jacocoTestReport {
        shouldRunAfter(test)
        shouldApplyExclusionsTo(classDirectories)

        reports {
            xml.required.set(true)
            html.required.set(true)
        }
    }

    jacocoTestCoverageVerification {
        shouldRunAfter(jacocoTestReport)
        shouldApplyExclusionsTo(classDirectories)

        violationRules {
            rule {
                limit {
                    minimum = 0.00.toBigDecimal()
                }
            }
        }
    }

    val jacocoTestReportAndVerification by registering {
        dependsOn(
            jacocoTestReport,
            jacocoTestCoverageVerification,
        )
        group = "verification"
    }

    if (providers.environmentVariable("CI").getOrNull() != "true") {
        check {
            finalizedBy(jacocoTestReportAndVerification)
        }
    }
}

fun shouldApplyExclusionsTo(classDirectories: ConfigurableFileCollection) {
    classDirectories.setFrom(
        files(
            classDirectories.files.map {
                fileTree(it) {
                    exclude(
                        "com/github/arhor/aws/graphql/federation/scheduler/**/config/",
                    )
                }
            }
        )
    )
}
