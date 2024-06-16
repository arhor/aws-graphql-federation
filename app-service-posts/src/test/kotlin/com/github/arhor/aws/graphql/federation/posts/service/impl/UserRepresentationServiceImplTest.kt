package com.github.arhor.aws.graphql.federation.posts.service.impl

import com.github.arhor.aws.graphql.federation.common.exception.EntityNotFoundException
import com.github.arhor.aws.graphql.federation.common.exception.Operation
import com.github.arhor.aws.graphql.federation.posts.data.entity.UserRepresentation
import com.github.arhor.aws.graphql.federation.posts.data.entity.UserRepresentation.UserFeature
import com.github.arhor.aws.graphql.federation.posts.data.entity.UserRepresentation.UserFeatures
import com.github.arhor.aws.graphql.federation.posts.data.repository.UserRepresentationRepository
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.DgsConstants.USER
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.User
import com.github.arhor.aws.graphql.federation.posts.util.Caches
import com.github.arhor.aws.graphql.federation.starter.testing.TEST_1_UUID_VAL
import com.github.arhor.aws.graphql.federation.starter.testing.ZERO_UUID_VAL
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchException
import org.assertj.core.api.Assertions.from
import org.assertj.core.api.InstanceOfAssertFactories.type
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.cache.CacheManager
import org.springframework.cache.concurrent.ConcurrentMapCache
import java.util.Optional

class UserRepresentationServiceImplTest {

    private val cache = ConcurrentMapCache(Caches.IDEMPOTENT_ID_SET.name)
    private val cacheManager = mockk<CacheManager>()
    private val userRepository = mockk<UserRepresentationRepository>()

    private lateinit var userService: UserRepresentationServiceImpl

    @BeforeEach
    fun setUp() {
        every { cacheManager.getCache(Caches.IDEMPOTENT_ID_SET.name) } returns cache

        userService = UserRepresentationServiceImpl(
            cacheManager,
            userRepository,
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

            every { userRepository.findAllById(any()) } returns listOf(userRepresentation)

            // When
            val result = userService.findUsersRepresentationsInBatch(expectedUserIds)

            // Then
            verify(exactly = 1) { userRepository.findAllById(expectedUserIds) }

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

            every { userRepository.findAllById(any()) } returns emptyList()

            // When
            val result = userService.findUsersRepresentationsInBatch(expectedUserIds)

            // Then
            verify(exactly = 1) { userRepository.findAllById(expectedUserIds) }

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
                shouldBePersisted = true,
            )

            every { userRepository.save(any()) } answers { firstArg() }

            // When
            for (i in 0..2) {
                userService.createUserRepresentation(USER_ID, IDEMPOTENCY_KEY)
            }

            // Then
            verify(exactly = 1) { userRepository.save(expectedUser) }
        }
    }

    @Nested
    @DisplayName("UserService :: deleteUserRepresentation")
    inner class DeleteUserRepresentationTest {
        @Test
        fun `should call userRepository deleteById only once with the same idempotencyKey`() {
            // Given
            every { userRepository.deleteById(any()) } just runs

            // When
            for (i in 0..2) {
                userService.deleteUserRepresentation(USER_ID, IDEMPOTENCY_KEY)
            }

            // Then
            verify(exactly = 1) { userRepository.deleteById(USER_ID) }
        }
    }

    @Nested
    @DisplayName("UserService :: toggleUserPosts")
    inner class SwitchUserPostsTest {
        @Test
        fun `should call userRepository#save when there is update applied to the user`() {
            // Given
            val user = UserRepresentation(id = USER_ID)

            every { userRepository.findById(any()) } returns Optional.of(user)
            every { userRepository.save(any()) } answers { firstArg() }

            // When
            val result = userService.toggleUserPosts(USER_ID)

            // Then
            verify(exactly = 1) { userRepository.findById(USER_ID) }
            verify(exactly = 1) { userRepository.save(user.copy(features = user.features + UserFeature.POSTS_DISABLED)) }

            assertThat(result)
                .isFalse()
        }

        @Test
        fun `should not call userRepository#save when there is no update applied to the user`() {
            // Given
            val user = UserRepresentation(id = USER_ID, features = UserFeatures(UserFeature.POSTS_DISABLED))

            every { userRepository.findById(any()) } returns Optional.of(user)
            every { userRepository.save(any()) } answers { firstArg() }

            // When
            val result = userService.toggleUserPosts(USER_ID)

            // Then
            verify(exactly = 1) { userRepository.findById(USER_ID) }
            verify(exactly = 1) { userRepository.save(user.copy(features = user.features - UserFeature.POSTS_DISABLED)) }

            assertThat(result)
                .isTrue()
        }

        @Test
        fun `should throw EntityNotFoundException when there is no user found by the input id`() {
            // Given
            every { userRepository.findById(any()) } returns Optional.empty()

            // When
            val result = catchException { userService.toggleUserPosts(USER_ID) }

            // Then
            verify(exactly = 1) { userRepository.findById(USER_ID) }

            assertThat(result)
                .asInstanceOf(type(EntityNotFoundException::class.java))
                .returns(USER.TYPE_NAME, from { it.entity })
                .returns("${USER.Id} = $USER_ID", from { it.condition })
                .returns(Operation.UPDATE, from { it.operation })
        }
    }

    companion object {
        private val USER_ID = ZERO_UUID_VAL
        private val IDEMPOTENCY_KEY = TEST_1_UUID_VAL
    }
}
