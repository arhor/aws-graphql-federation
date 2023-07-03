package com.github.arhor.dgs.comments.api.listener

import com.github.arhor.dgs.comments.service.CommentService
import com.ninjasquad.springmockk.MockkBean
import io.awspring.cloud.test.sqs.SqsTest
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.localstack.LocalStackContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import kotlin.time.Duration.Companion.seconds

@DirtiesContext
@Testcontainers(disabledWithoutDocker = true)
@SqsTest(listeners = [PostChangeSqsListener::class, UserChangeSqsListener::class])
internal class ExternalEntityStateChangeSqsListenerTest {

    @Autowired
    private lateinit var sqsClient: SqsAsyncClient

    @MockkBean
    private lateinit var mockCommentService: CommentService

    @Test
    fun `should call unlinkCommentsFromUser method on the CommentService on User Deleted event`() {
        // Given
        val userId = 1L
        val event = "{ \"id\": $userId }"

        every { mockCommentService.unlinkCommentsFromUser(any()) } returns 1

        // When
        sqsClient.sendMessage { it.queueUrl(userDeletedTestQueueUrl).messageBody(event) }

        // Then
        verify(timeout = 10.seconds.inWholeMilliseconds) { mockCommentService.unlinkCommentsFromUser(userId) }
    }

    @Test
    fun `should call deleteCommentsFromPost method on the CommentService on Post Deleted event`() {
        // Given
        val postId = 2L
        val event = "{ \"id\": $postId }"

        every { mockCommentService.deleteCommentsFromPost(any()) } returns 1

        // When
        sqsClient.sendMessage { it.queueUrl(postDeletedTestQueueUrl).messageBody(event) }

        // Then
        verify(timeout = 10.seconds.inWholeMilliseconds) { mockCommentService.deleteCommentsFromPost(postId) }
    }

    companion object {
        private const val USER_UPDATED_TEST_EVENTS_QUEUE = "user-updated-test-events"
        private const val USER_DELETED_TEST_EVENTS_QUEUE = "user-deleted-test-events"
        private const val POST_UPDATED_TEST_EVENTS_QUEUE = "post-updated-test-events"
        private const val POST_DELETED_TEST_EVENTS_QUEUE = "post-deleted-test-events"

        private lateinit var userUpdatedTestQueueUrl: String
        private lateinit var userDeletedTestQueueUrl: String
        private lateinit var postUpdatedTestQueueUrl: String
        private lateinit var postDeletedTestQueueUrl: String

        @JvmStatic
        @Container
        private val localstack =
            LocalStackContainer(DockerImageName.parse("localstack/localstack:1.4.0"))
                .withServices(LocalStackContainer.Service.SQS)
                .waitingFor(Wait.forHealthcheck())

        @JvmStatic
        @DynamicPropertySource
        fun registerDynamicProperties(registry: DynamicPropertyRegistry) {
            with(registry) {
                add("spring.cloud.aws.credentials.access-key") { localstack.accessKey }
                add("spring.cloud.aws.credentials.secret-key") { localstack.secretKey }
                add("spring.cloud.aws.region.static") { localstack.region }
                add("spring.cloud.aws.sqs.endpoint") { localstack.getEndpointOverride(LocalStackContainer.Service.SQS) }
                add("app-props.aws.sqs.user-updated-events-source") { USER_UPDATED_TEST_EVENTS_QUEUE }
                add("app-props.aws.sqs.user-deleted-events-source") { USER_DELETED_TEST_EVENTS_QUEUE }
                add("app-props.aws.sqs.post-updated-events-source") { POST_UPDATED_TEST_EVENTS_QUEUE }
                add("app-props.aws.sqs.post-deleted-events-source") { POST_DELETED_TEST_EVENTS_QUEUE }
            }
        }

        @JvmStatic
        @BeforeAll
        fun createQueues() = with(localstack) {
            userUpdatedTestQueueUrl = createQueue(USER_UPDATED_TEST_EVENTS_QUEUE)
            userDeletedTestQueueUrl = createQueue(USER_DELETED_TEST_EVENTS_QUEUE)
            postUpdatedTestQueueUrl = createQueue(POST_UPDATED_TEST_EVENTS_QUEUE)
            postDeletedTestQueueUrl = createQueue(POST_DELETED_TEST_EVENTS_QUEUE)
        }

        private fun LocalStackContainer.createQueue(name: String): String {
            val result = execInContainer("awslocal", "sqs", "create-queue", "--queue-name", name, "--output", "text")
            return result.stdout
        }
    }
}
