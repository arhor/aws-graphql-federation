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
            val userId = UUID.randomUUID()
            val userRepresentation = UserRepresentation(userId)
            val expectedResult = mapOf(userId to User(id = userRepresentation.id, postsOperable = true, postsDisabled = false))
            val expectedUserIds = setOf(userId)

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
            val user = User(
                id = UUID.randomUUID(),
                postsOperable = false
            )
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
