plugins {
    alias(libs.plugins.test.logger)
    alias(libs.plugins.dgs.codegen)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.kapt)
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
    maven { url = uri("https://repo.spring.io/milestone") }
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
    kapt(platform(":lib-platform"))
    kapt("org.springframework:spring-context-indexer")
    kapt("org.springframework.boot:spring-boot-configuration-processor")

    implementation(platform(":lib-platform"))
    implementation(":lib-common")
    implementation(":lib-spring-dgs")
    implementation(":lib-spring-starter-config")
    implementation(":lib-spring-starter-security")
    implementation(":lib-spring-starter-tracing")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.github.ben-manes.caffeine:caffeine")
    implementation("com.netflix.graphql.dgs:graphql-dgs-extended-scalars")
    implementation("com.netflix.graphql.dgs:graphql-dgs-spring-boot-starter")
    implementation("io.awspring.cloud:spring-cloud-aws-starter-sns")
    implementation("io.awspring.cloud:spring-cloud-aws-starter-sqs")
    implementation("io.awspring.cloud:spring-cloud-aws-starter-s3")
    implementation("org.flywaydb:flyway-core")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.retry:spring-retry")

    runtimeOnly("org.postgresql:postgresql")

    developmentOnly("org.springframework.boot:spring-boot-devtools")

    testImplementation("com.ninja-squad:springmockk")
    testImplementation("io.awspring.cloud:spring-cloud-aws-test")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:localstack")
    testImplementation("org.testcontainers:postgresql")
}

dependencyManagement {
    imports {
        mavenBom("com.netflix.graphql.dgs:graphql-dgs-platform-dependencies:${libs.versions.graphql.dgs.bom.get()}")
        mavenBom("io.awspring.cloud:spring-cloud-aws-dependencies:${libs.versions.spring.cloud.aws.get()}")
    }
}

tasks {
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
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

    withType<Test> {
        jvmArgs = listOf("-XX:+EnableDynamicAgentLoading")
        useJUnitPlatform()
    }

    processResources {
        filesMatching("application.yml") {
            expand(project.properties)
        }
    }

    generateJava {
        language = "kotlin"
        packageName = "com.github.arhor.aws.graphql.federation.posts.generated.graphql"
    }

    jacocoTestReport {
        shouldRunAfter(test)
        shouldApplyExclusionsTo(classDirectories)
    }

    jacocoTestCoverageVerification {
        shouldRunAfter(jacocoTestReport)
        shouldApplyExclusionsTo(classDirectories)

        violationRules {
            rule {
                limit {
                    minimum = 0.0.toBigDecimal()
                }
            }
        }
    }

    check {
        dependsOn(
            jacocoTestReport,
            jacocoTestCoverageVerification,
        )
    }
}

fun shouldApplyExclusionsTo(classDirectories: ConfigurableFileCollection) {
    classDirectories.setFrom(
        files(
            classDirectories.files.map {
                fileTree(it) {
                    exclude(
                        "com/github/arhor/aws/graphql/federation/posts/**/Main*.class",
                        "com/github/arhor/aws/graphql/federation/posts/**/aop/",
                        "com/github/arhor/aws/graphql/federation/posts/**/config/",
                        "com/github/arhor/aws/graphql/federation/posts/**/generated/",
                    )
                }
            }
        )
    )
}
