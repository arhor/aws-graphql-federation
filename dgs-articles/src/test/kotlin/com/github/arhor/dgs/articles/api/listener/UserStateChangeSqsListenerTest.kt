package com.github.arhor.dgs.articles.api.listener

import io.awspring.cloud.sqs.operations.SqsOperations
import io.awspring.cloud.test.sqs.SqsTest
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.support.GenericMessage
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.localstack.LocalStackContainer
import org.testcontainers.containers.localstack.LocalStackContainer.Service.SQS
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@SqsTest(UserStateChangeSqsListener::class)
@Testcontainers(disabledWithoutDocker = true)
internal class UserStateChangeSqsListenerTest {

    @Autowired
    private lateinit var sqs: SqsOperations

    @Test
    fun `should send an event to SQS then correctly consume it via UserStateChangeListener instance`() {
        // Given
        val event = GenericMessage("hello there", mapOf("test" to 1L))

        // When
        sqs.send(USER_DELETED_TEST_QUEUE, event)

        // Then
    }

    companion object {
        private const val USER_UPDATED_TEST_QUEUE = "user-updated-test-events-queue"
        private const val USER_DELETED_TEST_QUEUE = "user-deleted-test-events-queue"

        @JvmStatic
        @Container
        private val localstack =
            LocalStackContainer(DockerImageName.parse("localstack/localstack:1.4.0"))
                .withServices(SQS)

        @JvmStatic
        @DynamicPropertySource
        fun registerDynamicProperties(registry: DynamicPropertyRegistry) {
            with(registry) {
                add("spring.cloud.aws.credentials.access-key") { localstack.accessKey }
                add("spring.cloud.aws.credentials.secret-key") { localstack.secretKey }
                add("spring.cloud.aws.region.static") { localstack.region }
                add("spring.cloud.aws.sqs.endpoint") { localstack.getEndpointOverride(SQS) }
                add("app-props.aws.sqs.user-updated-events") { USER_UPDATED_TEST_QUEUE }
                add("app-props.aws.sqs.user-deleted-events") { USER_DELETED_TEST_QUEUE }
            }
        }

        @JvmStatic
        @BeforeAll
        fun createTestQueues() {
            with(localstack) {
                execInContainer("awslocal", "sqs", "create-queue", "--queue-name", USER_UPDATED_TEST_QUEUE)
                execInContainer("awslocal", "sqs", "create-queue", "--queue-name", USER_DELETED_TEST_QUEUE)
            }
        }
    }
}
