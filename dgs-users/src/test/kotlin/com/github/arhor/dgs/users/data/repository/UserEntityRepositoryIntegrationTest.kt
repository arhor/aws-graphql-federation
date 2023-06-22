package com.github.arhor.dgs.users.data.repository

import com.github.arhor.dgs.lib.config.ConfigureAdditionalBeans
import com.github.arhor.dgs.users.config.ConfigureDatabase
import com.github.arhor.dgs.users.data.entity.UserEntity
import com.ninjasquad.springmockk.MockkBean
import io.awspring.cloud.sns.core.SnsOperations
import io.mockk.every
import io.mockk.just
import io.mockk.runs
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.sql.Statement
import java.sql.Timestamp
import java.time.LocalDateTime

@DataJdbcTest
@DirtiesContext
@Testcontainers(disabledWithoutDocker = true)
@ContextConfiguration(classes = [ConfigureDatabase::class, ConfigureAdditionalBeans::class])
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
internal class UserEntityRepositoryIntegrationTest {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    @MockkBean
    private lateinit var snsOperations: SnsOperations

    @BeforeEach
    fun setUp() {
        every { snsOperations.sendNotification(any(), any()) } just runs
    }

    @Test
    fun `should return true for the email of an existing user`() {
        // Given
        createUserUsingJDBC(
            username = "test1@email.com",
            password = "TestPassword123",
        )

        // When
        val result = userRepository.existsByUsername("test1@email.com")

        // Then
        assertThat(result)
            .isTrue()
    }

//    @Test
//    fun `should return false for the email of a non-existing user`() {
//        // Given
//        val notPersistedUser = UserEntity(
//            username = "test2@email.com",
//            password = "TestPassword123",
//        )
//
//        // When
//        val result = userRepository.existsByUsername(notPersistedUser.username)
//
//        // Then
//        assertThat(result)
//            .isFalse()
//    }

//    @Test
//    fun `should send UserStateChangedMessage$Updated message on a new User entity save`() {
//        // Given
//        val newUser = UserEntity(
//            username = "test2@email.com",
//            password = "TestPassword123",
//        )
//        val stateChangedMessage = slot<SnsNotification<UserStateChange>>()
//
//        // When
//        val createdUser = userRepository.save(newUser)
//
//        // Then
//        verify(exactly = 1) { snsOperations.sendNotification(USER_UPDATED_TEST_EVENTS_DESTINATION, capture(stateChangedMessage)) }
//
//        assertThat(stateChangedMessage.captured)
//            .asInstanceOf(type(UserStateChangedMessage.Updated::class.java))
//            .returns(createdUser.id, from { it.userId })
//            .returns(createdUser.email, from { it.email })
//            .returns(createdUser.budget.limit, from { it.budgetLimit })
//    }

//    @Test
//    fun `should send UserStateChangedMessage$Updated message on an existing User entity save`() {
//        // Given
//        val userId = createUserUsingJDBC(
//            email = "test1@email.com",
//            password = "TestPassword123",
//            budgetLimit = BigDecimal("10.00")
//        )
//        val existingUser = userRepository.findByIdOrNull(userId)!!
//        val stateChangedMessage = slot<UserStateChangedMessage>()
//
//        // When
//        val updatedUser = userRepository.save(existingUser.copy(password = "UpdatedPassword123"))
//
//        // Then
//        verify(exactly = 1) { snsOperations.convertAndSend(USER_UPDATED_TEST_EVENTS_DESTINATION, capture(stateChangedMessage)) }
//
//        assertThat(stateChangedMessage.captured)
//            .asInstanceOf(type(UserStateChangedMessage.Updated::class.java))
//            .returns(updatedUser.id, from { it.userId })
//            .returns(updatedUser.email, from { it.email })
//            .returns(updatedUser.budget.limit, from { it.budgetLimit })
//    }

//    @Test
//    fun `should send UserStateChangedMessage$Deleted message on User entity delete`() {
//        // Given
//        val userId = createUserUsingJDBC(
//            email = "test1@email.com",
//            password = "TestPassword123",
//            budgetLimit = BigDecimal("10.00")
//        )
//        val existingUser = userRepository.findByIdOrNull(userId)!!
//        val stateChangedMessage = slot<UserStateChangedMessage>()
//
//        // When
//        userRepository.delete(existingUser)
//
//        // Then
//        verify(exactly = 1) { snsOperations.convertAndSend(USER_DELETED_TEST_EVENTS_DESTINATION, capture(stateChangedMessage)) }
//
//        assertThat(stateChangedMessage.captured)
//            .asInstanceOf(type(UserStateChangedMessage.Deleted::class.java))
//            .returns(userId, from { it.userId })
//    }

//    @Test
//    fun `should send UserStateChangedMessage$Deleted message on User entity delete by id`() {
//        // Given
//        val userId = createUserUsingJDBC(
//            email = "test1@email.com",
//            password = "TestPassword123",
//            budgetLimit = BigDecimal("10.00")
//        )
//        val stateChangedMessage = slot<UserStateChangedMessage>()
//
//        // When
//        userRepository.deleteById(userId)
//
//        // Then
//        verify(exactly = 1) { snsOperations.convertAndSend(USER_DELETED_TEST_EVENTS_DESTINATION, capture(stateChangedMessage)) }
//
//        assertThat(stateChangedMessage.captured)
//            .asInstanceOf(type(UserStateChangedMessage.Deleted::class.java))
//            .returns(userId, from { it.userId })
//    }

    @Suppress("UNUSED_CHANGED_VALUE", "SameParameterValue")
    private fun createUserUsingJDBC(username: String, password: String): Long {
        val initialVersion = 1L
        val currentTimestamp = Timestamp.valueOf(LocalDateTime.now())
        val generatedKeyHolder = GeneratedKeyHolder()

        jdbcTemplate.update({
            it.prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS).apply {
                var idx = 1
                // @formatter:off
                    setString(idx++, username)
                    setString(idx++, password)
                      setLong(idx++, initialVersion)
                 setTimestamp(idx++, currentTimestamp)
                // @formatter:on
            }
        }, generatedKeyHolder)

        return generatedKeyHolder.keys!!["id"] as Long
    }

    companion object {
        /* language=SQL */
        private val INSERT_QUERY = """
            INSERT INTO ${UserEntity.TABLE_NAME} (
                "username",
                "password",
                "version",
                "created_date_time"
            ) VALUES (?, ?, ?, ?);
        """.trimIndent()

        @JvmStatic
        @Container
        private val db = PostgreSQLContainer("postgres:12")

        @JvmStatic
        @DynamicPropertySource
        fun registerDynamicProperties(registry: DynamicPropertyRegistry) {
            with(registry) {
                add("spring.datasource.url", db::getJdbcUrl)
                add("spring.datasource.username", db::getUsername)
                add("spring.datasource.password", db::getPassword)
                add("application-props.aws.sns.user-updated-events") { "user-updated-test-events" }
                add("application-props.aws.sns.user-deleted-events") { "user-deleted-test-events" }
            }
        }
    }
}
