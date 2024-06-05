plugins {
    alias(libs.plugins.test.logger)
    alias(libs.plugins.dgs.codegen)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.deps)
    jacoco
    java
}

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

testlogger {
    showStandardStreams = true
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
    }
    all {
        exclude(group = "org.apache.logging.log4j", module = "log4j-to-slf4j")
        exclude(group = "org.apache.logging.log4j", module = "log4j-api")
    }
}

dependencies {
    annotationProcessor(platform(":lib-platform"))
    annotationProcessor("org.projectlombok:lombok")
    annotationProcessor("org.springframework:spring-context-indexer")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    implementation(platform(":lib-platform"))
    implementation(":lib-common")
    implementation(":lib-starter-core")
    implementation(":lib-starter-graphql")
    implementation(":lib-spring-starter-security")
    implementation(":lib-spring-starter-tracing")
    implementation("com.github.ben-manes.caffeine:caffeine")
    implementation("com.netflix.graphql.dgs:graphql-dgs-extended-validation")
    implementation("com.netflix.graphql.dgs:graphql-dgs-extended-scalars")
    implementation("com.netflix.graphql.dgs:graphql-dgs-spring-boot-starter")
    implementation("io.awspring.cloud:spring-cloud-aws-starter-sns")
    implementation("io.awspring.cloud:spring-cloud-aws-starter-sqs")
    implementation("org.flywaydb:flyway-core")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.retry:spring-retry")

    compileOnly("org.projectlombok:lombok")

    runtimeOnly("org.postgresql:postgresql")

    developmentOnly("org.springframework.boot:spring-boot-devtools")

    testImplementation("com.tngtech.archunit:archunit-junit5")
    testImplementation("io.awspring.cloud:spring-cloud-aws-test")
    testImplementation("org.awaitility:awaitility")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:localstack")
    testImplementation("org.testcontainers:postgresql")
}

dependencyManagement {
    imports {
        mavenBom(libs.bom.graphql.dgs.get().toString())
        mavenBom(libs.bom.spring.cloud.aws.get().toString())
    }
}

tasks {
    withType<JavaCompile> {
        options.compilerArgs.addAll(
            listOf(
                "-Xlint:unchecked",
                "-Xlint:deprecation",
                "-Xlint:preview",
                "-parameters",
            )
        )
    }

    withType<Test> {
        jvmArgs = listOf("-XX:+EnableDynamicAgentLoading")
        systemProperty("spring.profiles.active", "test")
        useJUnitPlatform()
    }

    processResources {
        filesMatching("application.yml") {
            expand(project.properties)
        }
    }

    generateJava {
        language = "java"
        packageName = "com.github.arhor.aws.graphql.federation.comments.generated.graphql"
        typeMapping = mutableMapOf(
            "UUID" to "java.util.UUID"
        )
        addGeneratedAnnotation = true
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
                    minimum = 0.80.toBigDecimal()
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
                        "com/github/arhor/aws/graphql/federation/comments/**/config/",
                        "com/github/arhor/aws/graphql/federation/comments/**/generated/",
                    )
                }
            }
        )
    )
}
