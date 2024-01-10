@file:Suppress("ClassName", "SameParameterValue")

package com.github.arhor.aws.graphql.federation.users.service

import com.github.arhor.aws.graphql.federation.common.exception.EntityDuplicateException
import com.github.arhor.aws.graphql.federation.common.exception.Operation
import com.github.arhor.aws.graphql.federation.users.data.entity.UserEntity
import com.github.arhor.aws.graphql.federation.users.data.repository.AuthRepository
import com.github.arhor.aws.graphql.federation.users.data.repository.UserRepository
import com.github.arhor.aws.graphql.federation.users.generated.graphql.DgsConstants.USER
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.CreateUserInput
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.DeleteUserInput
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.UpdateUserInput
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.User
import com.github.arhor.aws.graphql.federation.users.service.events.UserEventEmitter
import com.github.arhor.aws.graphql.federation.users.service.impl.UserServiceImpl
import com.github.arhor.aws.graphql.federation.users.service.mapping.UserMapper
import io.mockk.Call
import io.mockk.MockKAnswerScope
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchException
import org.assertj.core.api.Assertions.from
import org.assertj.core.api.InstanceOfAssertFactories.throwable
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.password.PasswordEncoder

internal class UserServiceTest {

    private val userMapper: UserMapper = mockk()
    private val authRepository: AuthRepository = mockk()
    private val userRepository: UserRepository = mockk()
    private val userEventEmitter: UserEventEmitter = mockk()
    private val passwordEncoder: PasswordEncoder = mockk()

    private val userService = UserServiceImpl(
        userMapper,
        authRepository,
        userRepository,
        userEventEmitter,
        passwordEncoder,
    )

    @Nested
    inner class `UserService # createUser` {
        @Test
        fun `should correctly create new user entity and return DTO with assigned id`() {
            // Given
            val expectedId = 1L
            val expectedUsername = "test@email.com"
            val expectedPassword = "TestPassword123"

            val input = CreateUserInput(
                username = expectedUsername,
                password = expectedPassword,
            )

            every { userRepository.existsByUsername(any()) } returns false
            every { passwordEncoder.encode(any()) } answers { firstArg() }
            every { userMapper.mapToEntity(any()) } answers convertingDtoToUser
            every { userRepository.save(any()) } answers copyingUserWithAssignedId(id = expectedId)
            every { userMapper.mapToResult(any()) } answers convertingUserToDto

            // When
            val result = userService.createUser(input)

            // Then
            assertThat(result.user)
                .returns(expectedId, from { it.id })
                .returns(expectedUsername, from { it.username })

            verify(exactly = 1) { userRepository.existsByUsername(any()) }
            verify(exactly = 1) { userMapper.mapToEntity(any()) }
            verify(exactly = 1) { userRepository.save(any()) }
            verify(exactly = 1) { userMapper.mapToResult(any()) }
        }

        @Test
        fun `should throw EntityDuplicateException creating user with already taken username`() {
            // Given
            val input = CreateUserInput(
                username = "test-username",
                password = "test-password",
            )

            val expectedEntity = USER.TYPE_NAME
            val expectedOperation = Operation.CREATE
            val expectedCondition = "${USER.Username} = ${input.username}"
            val expectedExceptionType = EntityDuplicateException::class.java

            val username = slot<String>()

            every { userRepository.existsByUsername(capture(username)) } returns true

            // When
            val result = catchException { userService.createUser(input) }

            // Then
            assertThat(username)
                .returns(true, from { it.isCaptured })
                .returns(input.username, from { it.captured })

            assertThat(result)
                .asInstanceOf(throwable(expectedExceptionType))
                .satisfies(
                    { assertThat(it.entity).describedAs("entity").isEqualTo(expectedEntity) },
                    { assertThat(it.operation).describedAs("operation").isEqualTo(expectedOperation) },
                    { assertThat(it.condition).describedAs("condition").isEqualTo(expectedCondition) },
                )
        }
    }

    @Nested
    inner class `UserService # updateUser` {

        @Test
        fun `should save updated user state to repository when there are actual changes`() {
            // Given
            val user = UserEntity(
                id = 1,
                username = "test-username",
                password = "test-password",
            )

            every { userRepository.findByIdOrNull(any()) } returns user
            every { userRepository.save(any()) } answers { firstArg() }
            every { userMapper.mapToResult(any()) } answers convertingUserToDto
            every { passwordEncoder.encode(any()) } answers { firstArg() }

            // When
            userService.updateUser(
                input = UpdateUserInput(
                    id = user.id!!,
                    password = "${user.password}-updated",
                )
            )

            // Then
            verify(exactly = 1) { userRepository.save(any()) }
        }

        @Test
        fun `should not call save method on repository when there are no changes in user state`() {
            // Given
            val user = UserEntity(
                id = 1,
                username = "test-username",
                password = "test-password",
            )

            every { userRepository.findByIdOrNull(any()) } returns user
            every { userRepository.save(any()) } answers { firstArg() }
            every { userMapper.mapToResult(any()) } answers convertingUserToDto
            every { passwordEncoder.encode(any()) } answers { firstArg() }

            // When
            userService.updateUser(
                input = UpdateUserInput(
                    id = user.id!!,
                    password = user.password,
                )
            )

            // Then
            verify(exactly = 0) { userRepository.save(any()) }
        }
    }

    @Nested
    inner class `UserService # deleteUser` {

        @Test
        fun `should return expected result deleting user`() {
            // Given
            val expectedId = 1L

            every { userRepository.findByIdOrNull(any()) } returns mockk { every { id } returns expectedId }
            every { userRepository.delete(any()) } just runs
            every { userEventEmitter.emit(any()) } just runs

            // When
            userService.deleteUser(DeleteUserInput(expectedId))

            // Then
            verify(exactly = 1) { userRepository.findByIdOrNull(any()) }
            verify(exactly = 1) { userRepository.delete(any()) }
            verify(exactly = 1) { userEventEmitter.emit(any()) }

            confirmVerified(userMapper, userRepository, userEventEmitter, passwordEncoder)
        }
    }

    private val convertingDtoToUser: MockKAnswerScope<UserEntity, *>.(Call) -> UserEntity
        get() = {
            firstArg<CreateUserInput>().let {
                UserEntity(
                    username = it.username,
                    password = it.password,
                )
            }
        }

    private val convertingUserToDto: MockKAnswerScope<User, *>.(Call) -> User
        get() = {
            firstArg<UserEntity>().let {
                User(
                    id = it.id!!,
                    username = it.username,
                )
            }
        }

    private fun copyingUserWithAssignedId(id: Long): MockKAnswerScope<UserEntity, *>.(Call) -> UserEntity = {
        firstArg<UserEntity>().copy(id = id)
    }
}
