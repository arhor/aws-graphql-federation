package com.github.arhor.dgs.comments.api.listener

import org.junit.jupiter.api.BeforeAll
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.localstack.LocalStackContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@DirtiesContext
@Testcontainers(disabledWithoutDocker = true)
internal abstract class BaseSqsListenerTest {

    companion object {
        const val USER_UPDATED_TEST_EVENTS_QUEUE = "user-updated-test-events"
        const val USER_DELETED_TEST_EVENTS_QUEUE = "user-deleted-test-events"
        const val POST_UPDATED_TEST_EVENTS_QUEUE = "post-updated-test-events"
        const val POST_DELETED_TEST_EVENTS_QUEUE = "post-deleted-test-events"

        @JvmStatic
        @Container
        private val localstack =
            LocalStackContainer(DockerImageName.parse("localstack/localstack:1.4.0"))
                .withServices(LocalStackContainer.Service.SQS)

        @JvmStatic
        @DynamicPropertySource
        fun registerDynamicProperties(registry: DynamicPropertyRegistry) {
            with(registry) {
                add("spring.cloud.aws.credentials.access-key") { localstack.accessKey }
                add("spring.cloud.aws.credentials.secret-key") { localstack.secretKey }
                add("spring.cloud.aws.region.static") { localstack.region }
                add("spring.cloud.aws.sqs.endpoint") { localstack.getEndpointOverride(LocalStackContainer.Service.SQS) }
                add("app-props.aws.sqs.user-updates") { USER_UPDATED_TEST_EVENTS_QUEUE }
                add("app-props.aws.sqs.user-deletes") { USER_DELETED_TEST_EVENTS_QUEUE }
                add("app-props.aws.sqs.post-updates") { POST_UPDATED_TEST_EVENTS_QUEUE }
                add("app-props.aws.sqs.post-deletes") { POST_DELETED_TEST_EVENTS_QUEUE }
            }
        }

        @JvmStatic
        @BeforeAll
        fun createTestQueues() {
            with(localstack) {
                execInContainer("awslocal", "sqs", "create-queue", "--queue-name", USER_UPDATED_TEST_EVENTS_QUEUE)
                execInContainer("awslocal", "sqs", "create-queue", "--queue-name", USER_DELETED_TEST_EVENTS_QUEUE)
                execInContainer("awslocal", "sqs", "create-queue", "--queue-name", POST_UPDATED_TEST_EVENTS_QUEUE)
                execInContainer("awslocal", "sqs", "create-queue", "--queue-name", POST_DELETED_TEST_EVENTS_QUEUE)
            }
        }
    }
}
