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
import org.assertj.core.api.Assertions.from
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.cache.CacheManager
import org.springframework.cache.concurrent.ConcurrentMapCache
import java.util.Optional
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
    @DisplayName("UserService :: findUserRepresentation")
    inner class FindUserRepresentationTest {
        @Test
        fun `should return expected user when it exists by id`() {
            // Given
            val userId = UUID.randomUUID()

            every { userRepresentationRepository.findById(any()) } returns Optional.of(UserRepresentation(userId))

            // When
            val result = userService.findUserRepresentation(userId)

            // Then
            verify(exactly = 1) { userRepresentationRepository.findById(userId) }

            assertThat(result)
                .isNotNull()
                .returns(userId, from { it.id })
        }

        @Test
        fun `should return user with postsOperable false when user does not exist by id`() {
            // Given
            val expectedUser = User(
                id = UUID.randomUUID(),
                postsOperable = false
            )

            every { userRepresentationRepository.findById(any()) } returns Optional.empty()

            // When
            val result = userService.findUserRepresentation(expectedUser.id)

            // Then
            verify(exactly = 1) { userRepresentationRepository.findById(expectedUser.id) }

            assertThat(result)
                .isNotNull()
                .isEqualTo(expectedUser)
        }
    }

    @Nested
    @DisplayName("UserService :: createUserRepresentation")
    inner class CreateUserRepresentationTest {
        @Test
        fun `should call userRepository save only once with the same idempotencyKey`() {
            // Given
            val idempotencyKey = UUID.randomUUID()
            val userId = UUID.randomUUID()
            val expectedUser = UserRepresentation(
                id = userId,
                postsDisabled = false,
                shouldBePersisted = true,
            )

            every { userRepresentationRepository.save(any()) } answers { firstArg() }

            // When
            for (i in 0..2) {
                userService.createUserRepresentation(userId, idempotencyKey)
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
            val idempotencyKey = UUID.randomUUID()
            val userId = UUID.randomUUID()

            every { userRepresentationRepository.deleteById(any()) } just runs

            // When
            for (i in 0..2) {
                userService.deleteUserRepresentation(userId, idempotencyKey)
            }

            // Then
            verify(exactly = 1) { userRepresentationRepository.deleteById(userId) }
        }
    }
}
