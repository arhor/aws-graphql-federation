package com.github.arhor.dgs.articles

import com.github.arhor.dgs.comments.UserStateChangeListener
import com.ninjasquad.springmockk.MockkBean
import io.awspring.cloud.sqs.operations.SqsOperations
import io.awspring.cloud.test.sqs.SqsTest
import io.mockk.verify
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.support.GenericMessage
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.localstack.LocalStackContainer
import org.testcontainers.containers.localstack.LocalStackContainer.Service.SQS
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import java.util.function.Consumer

@SqsTest(UserStateChangeListener::class)
@Testcontainers(disabledWithoutDocker = true)
internal class UserEventListenerIntegrationTest {

    @Autowired
    private lateinit var sqs: SqsOperations

    @MockkBean
    private lateinit var consumer: Consumer<String>

    @Test
    fun `should send an event to SQS then correctly consume it via UserStateChangeListener instance`() {
        logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
        // Given
        val event = GenericMessage("hello there", mapOf("test" to 1L))

        // When
        sqs.send(USER_DELETED_TEST_QUEUE, event)

        // Then
        verify(exactly = 1, timeout = 3000) {
            logger.info("---------------------------------------------------------------------------------------------")
            consumer.accept("SUCCESS")
            logger.info("---------------------------------------------------------------------------------------------")
        }
        logger.info("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<")
    }

    companion object {
        private const val USER_UPDATED_TEST_QUEUE = "user-updated-test-queue"
        private const val USER_DELETED_TEST_QUEUE = "user-deleted-test-queue"

        private val logger = LoggerFactory.getLogger(UserEventListenerIntegrationTest::class.java)

        @JvmStatic
        @Container
        private val localstack =
            LocalStackContainer(DockerImageName.parse("localstack/localstack:1.4.0"))
                .withServices(SQS)

        @JvmStatic
        @DynamicPropertySource
        fun registerDynamicProperties(registry: DynamicPropertyRegistry) {
            logger.info("---------------------------------------------------------------------------------------------")
            with(registry) {
                add("spring.cloud.aws.credentials.access-key") { localstack.accessKey }
                add("spring.cloud.aws.credentials.secret-key") { localstack.secretKey }
                add("spring.cloud.aws.region.static") { localstack.region }
                add("spring.cloud.aws.sqs.endpoint") { localstack.getEndpointOverride(SQS) }
                add("app-props.aws.user-updated-queue") { USER_UPDATED_TEST_QUEUE }
                add("app-props.aws.user-deleted-queue") { USER_DELETED_TEST_QUEUE }
            }
            logger.info("---------------------------------------------------------------------------------------------")
        }

        @JvmStatic
        @BeforeAll
        fun createTestQueues() {
            logger.info("---------------------------------------------------------------------------------------------")
            with(localstack) {
                logger.info(
                    "{}",
                    execInContainer("awslocal", "sqs", "create-queue", "--queue-name", USER_UPDATED_TEST_QUEUE)
                )
                logger.info(
                    "{}",
                    execInContainer("awslocal", "sqs", "create-queue", "--queue-name", USER_DELETED_TEST_QUEUE)
                )
            }
            logger.info("---------------------------------------------------------------------------------------------")
        }

//        @JvmStatic
//        @AfterAll
//        fun tearDownClass(@Autowired messageListenerContainer: MessageListenerContainer<*>) {
//            messageListenerContainer.stop()
//        }
    }
}
