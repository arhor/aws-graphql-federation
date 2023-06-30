package com.github.arhor.dgs.comments.api.listener

import com.github.arhor.dgs.comments.service.CommentService
import com.ninjasquad.springmockk.MockkBean
import io.awspring.cloud.sqs.operations.SqsOperations
import io.awspring.cloud.test.sqs.SqsTest
import io.mockk.verify
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

@SqsTest(PostChangeSqsListener::class)
@Testcontainers(disabledWithoutDocker = true)
internal class PostChangeSqsListenerTest {

    @Autowired
    private lateinit var sqs: SqsOperations

    @MockkBean
    private lateinit var mockCommentService: CommentService

    @Test
    fun `should call deleteCommentsFromPost method on the CommentService on Post Deleted event`() {
        // Given
        val postId = 1L
        val event = PostChangeSqsListener.PostChange.Deleted(id = postId)

        // When
        sqs.send(POST_DELETED_TEST_EVENTS_QUEUE, GenericMessage(event))

        // Then
        verify(exactly = 1, timeout = 3000) { mockCommentService.deleteCommentsFromPost(postId) }
    }

    companion object {
        private const val POST_UPDATED_TEST_EVENTS_QUEUE = "post-updated-test-events"
        private const val POST_DELETED_TEST_EVENTS_QUEUE = "post-deleted-test-events"

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
                add("app-props.aws.post-updates") { POST_UPDATED_TEST_EVENTS_QUEUE }
                add("app-props.aws.post-deletes") { POST_DELETED_TEST_EVENTS_QUEUE }
            }
        }

        @JvmStatic
        @BeforeAll
        fun createTestQueues() {
            with(localstack) {
                execInContainer("awslocal", "sqs", "create-queue", "--queue-name", POST_UPDATED_TEST_EVENTS_QUEUE)
                execInContainer("awslocal", "sqs", "create-queue", "--queue-name", POST_DELETED_TEST_EVENTS_QUEUE)
            }
        }
    }
}
