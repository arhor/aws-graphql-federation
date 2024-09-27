package com.github.arhor.aws.graphql.federation.posts.data.repository

import com.github.arhor.aws.graphql.federation.posts.data.model.OutboxMessageEntity
import com.github.arhor.aws.graphql.federation.posts.data.model.callback.OutboxMessageEntityCallback
import com.github.arhor.aws.graphql.federation.starter.testing.ZERO_UUID_VAL
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.support.TransactionTemplate
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

@ContextConfiguration(classes = [OutboxMessageEntityCallback::class])
class OutboxMessageRepositoryTest : RepositoryTestBase() {

    @Autowired
    private lateinit var outboxMessageRepository: OutboxMessageRepository

    @Autowired
    private lateinit var transactionManager: PlatformTransactionManager

    @AfterEach
    fun cleanUpDatabase() {
        outboxMessageRepository.deleteAll()
    }

    @Test
    fun `should read outbox events with lock preventing them to be red by other transactions`() {
        // Given
        val expectedSizeOfBatch = 5
        val expectedEventsAtAll = expectedSizeOfBatch * 2
        val transactionTemplate = TransactionTemplate(transactionManager, TestConcurrentTransactionDefinition)

        transactionTemplate.executeWithoutResult {
            outboxMessageRepository.saveAll(
                (1..expectedEventsAtAll).map {
                    OutboxMessageEntity(
                        type = "test-event",
                        data = emptyMap(),
                        traceId = ZERO_UUID_VAL,
                    )
                }
            )
        }

        // When
        val (outboxEvents1, outboxEvents2) = transactionTemplate.executeConcurrently(
            task(runThenWait = 2.0.seconds) {
                outboxMessageRepository.findOldestMessagesWithLock(expectedSizeOfBatch)
            },
            task(waitThenRun = 0.5.seconds) {
                outboxMessageRepository.findOldestMessagesWithLock(expectedSizeOfBatch)
            }
        )

        // Then
        assertThat(outboxEvents1)
            .hasSize(expectedSizeOfBatch)

        assertThat(outboxEvents2)
            .hasSize(expectedSizeOfBatch)

        assertThat(outboxEvents1)
            .doesNotContainAnyElementsOf(outboxEvents2)
    }

    private fun <T> task(waitThenRun: Duration? = null, runThenWait: Duration? = null, job: () -> T): () -> T = {
        waitThenRun?.toJavaDuration()?.run(Thread::sleep)
        val result = job.invoke()
        runThenWait?.toJavaDuration()?.run(Thread::sleep)
        result
    }

    private fun <T> TransactionTemplate.executeConcurrently(vararg jobs: () -> T): List<T> =
        Executors.newVirtualThreadPerTaskExecutor().use { executor ->
            jobs.map { job -> Callable { execute { job() } } }
                .let { executor.invokeAll(it, 10, TimeUnit.SECONDS) }
                .map { it.get() }
        }

    private object TestConcurrentTransactionDefinition : TransactionDefinition {
        override fun getIsolationLevel() = TransactionDefinition.ISOLATION_READ_COMMITTED
        override fun getPropagationBehavior() = TransactionDefinition.PROPAGATION_REQUIRES_NEW
    }
}
