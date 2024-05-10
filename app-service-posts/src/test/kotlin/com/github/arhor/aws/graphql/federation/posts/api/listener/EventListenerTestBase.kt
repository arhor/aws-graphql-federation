package com.github.arhor.aws.graphql.federation.posts.api.listener

import io.awspring.cloud.sqs.operations.SqsTemplate
import io.awspring.cloud.test.sqs.SqsTest
import org.junit.jupiter.api.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.localstack.LocalStackContainer
import org.testcontainers.containers.localstack.LocalStackContainer.Service.SQS
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@Tag("integration")
@SqsTest
@DirtiesContext
@Testcontainers
abstract class EventListenerTestBase {

    @Autowired
    protected lateinit var sqsTemplate: SqsTemplate

    companion object {
        @JvmStatic
        @Container
        private val localStack = LocalStackContainer(
            DockerImageName.parse("localstack/localstack:3.4")
        )

        @JvmStatic
        @DynamicPropertySource
        fun registerDynamicProperties(registry: DynamicPropertyRegistry) = with(registry) {
            add("spring.cloud.aws.credentials.access-key") { localStack.accessKey }
            add("spring.cloud.aws.credentials.secret-key") { localStack.secretKey }
            add("spring.cloud.aws.region.static") { localStack.region }
            add("spring.cloud.aws.sqs.endpoint") { localStack.getEndpointOverride(SQS).toString() }
        }

        @JvmStatic
        protected fun createdQueue(queueName: String) {
            localStack.execInContainer("awslocal", "sqs", "create-queue", "--queue-name", queueName)
        }
    }
}

