package com.github.arhor.aws.graphql.federation.posts.api.listener

import com.github.arhor.aws.graphql.federation.common.event.DomainEvent.Companion.HEADER_IDEMPOTENCY_ID
import com.github.arhor.aws.graphql.federation.common.event.UserEvent
import com.github.arhor.aws.graphql.federation.posts.service.UserService
import com.ninjasquad.springmockk.MockkBean
import io.awspring.cloud.sqs.operations.SqsTemplate
import io.awspring.cloud.test.sqs.SqsTest
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.support.GenericMessage
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.localstack.LocalStackContainer
import org.testcontainers.containers.localstack.LocalStackContainer.Service.SQS
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import java.util.UUID
import kotlin.time.Duration.Companion.seconds

@Tag("integration")
@SqsTest
@DirtiesContext
@Testcontainers
@ContextConfiguration(classes = [UserEventListener::class])
class UserEventListenerTest {

    @MockkBean
    private lateinit var userService: UserService

    @Autowired
    private lateinit var sqsTemplate: SqsTemplate

    @AfterEach
    fun tearDown() {
        confirmVerified(userService)
    }

    @Test
    fun `should call createInternalUserRepresentation on UserEvent#Created`() {
        // Given
        val event = UserEvent.Created(id = UUID.randomUUID())

        every { userService.createInternalUserRepresentation(any()) } just runs

        // When
        sqsTemplate.send(
            USER_CREATED_TEST_QUEUE,
            GenericMessage(
                event,
                mapOf(HEADER_IDEMPOTENCY_ID to UUID.randomUUID())
            )
        )

        // Then
        verify(exactly = 1, timeout = 5.seconds.inWholeMilliseconds) {
            userService.createInternalUserRepresentation(event.id)
        }
    }

    @Test
    fun `should call deleteInternalUserRepresentation on UserEvent#Deleted`() {
        // Given
        val event = UserEvent.Deleted(id = UUID.randomUUID())

        every { userService.deleteInternalUserRepresentation(any()) } just runs

        // When
        sqsTemplate.send(
            USER_DELETED_TEST_QUEUE,
            GenericMessage(
                event,
                mapOf(HEADER_IDEMPOTENCY_ID to UUID.randomUUID())
            )
        )

        // Then
        verify(exactly = 1, timeout = 5.seconds.inWholeMilliseconds) {
            userService.deleteInternalUserRepresentation(event.id)
        }
    }

    companion object {
        private const val USER_CREATED_TEST_QUEUE = "user-created-test-queue"
        private const val USER_DELETED_TEST_QUEUE = "user-deleted-test-queue"

        @JvmStatic
        @Container
        val localStack = LocalStackContainer(
            DockerImageName.parse("localstack/localstack:3.4")
        )

        @JvmStatic
        @DynamicPropertySource
        fun registerDynamicProperties(registry: DynamicPropertyRegistry) = with(registry) {
            add("spring.cloud.aws.region.static") { localStack.region }
            add("spring.cloud.aws.sqs.endpoint") { localStack.getEndpointOverride(SQS).toString() }
            add("spring.cloud.aws.credentials.access-key") { localStack.accessKey }
            add("spring.cloud.aws.credentials.secret-key") { localStack.secretKey }
            add("app-props.aws.sqs.user-created-events") { USER_CREATED_TEST_QUEUE }
            add("app-props.aws.sqs.user-deleted-events") { USER_DELETED_TEST_QUEUE }
        }

        @JvmStatic
        @BeforeAll
        fun createdTestQueues() {
            with(localStack) {
                execInContainer("awslocal", "sqs", "create-queue", "--queue-name", USER_CREATED_TEST_QUEUE)
                execInContainer("awslocal", "sqs", "create-queue", "--queue-name", USER_DELETED_TEST_QUEUE)
            }
        }
    }
}
