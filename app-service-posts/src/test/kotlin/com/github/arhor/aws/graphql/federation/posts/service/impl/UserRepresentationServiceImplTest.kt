package com.github.arhor.aws.graphql.federation.posts.service.impl

import com.github.arhor.aws.graphql.federation.posts.data.entity.UserRepresentation
import com.github.arhor.aws.graphql.federation.posts.data.repository.UserRepresentationRepository
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.User
import com.github.arhor.aws.graphql.federation.posts.util.Caches
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.cache.CacheManager
import org.springframework.cache.concurrent.ConcurrentMapCache
import java.util.UUID

class UserRepresentationServiceImplTest {

    private val cache = ConcurrentMapCache(Caches.IDEMPOTENT_ID_SET.name)
    private val cacheManager = mockk<CacheManager>()
    private val userRepresentationRepository = mockk<UserRepresentationRepository>()

    private lateinit var userService: UserRepresentationServiceImpl

    @BeforeEach
    fun setUp() {
        every { cacheManager.getCache(Caches.IDEMPOTENT_ID_SET.name) } returns cache

        userService = UserRepresentationServiceImpl(
            cacheManager,
            userRepresentationRepository,
        )
        userService.initialize()
    }


    @Nested
    @DisplayName("UserService :: findUsersRepresentationsInBatch")
    inner class FindUserRepresentationTest {
        @Test
        fun `should return expected user when it exists by id`() {
            // Given
            val userRepresentation = UserRepresentation(USER_ID)
            val expectedResult = mapOf(USER_ID to User(id = userRepresentation.id, postsDisabled = false))
            val expectedUserIds = setOf(USER_ID)

            every { userRepresentationRepository.findAllById(any()) } returns listOf(userRepresentation)

            // When
            val result = userService.findUsersRepresentationsInBatch(expectedUserIds)

            // Then
            verify(exactly = 1) { userRepresentationRepository.findAllById(expectedUserIds) }

            assertThat(result)
                .isNotNull()
                .isEqualTo(expectedResult)
        }

        @Test
        fun `should return user with postsOperable false when user does not exist by id`() {
            // Given
            val user = User(id = USER_ID)
            val expectedResult = mapOf(user.id to user)
            val expectedUserIds = setOf(user.id)

            every { userRepresentationRepository.findAllById(any()) } returns emptyList()

            // When
            val result = userService.findUsersRepresentationsInBatch(expectedUserIds)

            // Then
            verify(exactly = 1) { userRepresentationRepository.findAllById(expectedUserIds) }

            assertThat(result)
                .isNotNull()
                .isEqualTo(expectedResult)
        }
    }

    @Nested
    @DisplayName("UserService :: createUserRepresentation")
    inner class CreateUserRepresentationTest {
        @Test
        fun `should call userRepository save only once with the same idempotencyKey`() {
            // Given
            val expectedUser = UserRepresentation(
                id = USER_ID,
                postsDisabled = false,
                shouldBePersisted = true,
            )

            every { userRepresentationRepository.save(any()) } answers { firstArg() }

            // When
            for (i in 0..2) {
                userService.createUserRepresentation(USER_ID, IDEMPOTENCY_KEY)
            }

            // Then
            verify(exactly = 1) { userRepresentationRepository.save(expectedUser) }
        }
    }

    @Nested
    @DisplayName("UserService :: deleteUserRepresentation")
    inner class DeleteUserRepresentationTest {
        @Test
        fun `should call userRepository deleteById only once with the same idempotencyKey`() {
            // Given
            every { userRepresentationRepository.deleteById(any()) } just runs

            // When
            for (i in 0..2) {
                userService.deleteUserRepresentation(USER_ID, IDEMPOTENCY_KEY)
            }

            // Then
            verify(exactly = 1) { userRepresentationRepository.deleteById(USER_ID) }
        }
    }

    companion object {
        private val USER_ID = UUID.randomUUID()
        private val IDEMPOTENCY_KEY = UUID.randomUUID()
    }
}
