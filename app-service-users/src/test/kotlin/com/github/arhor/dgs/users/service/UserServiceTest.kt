@file:Suppress("ClassName", "SameParameterValue")

package com.github.arhor.dgs.users.service

import com.github.arhor.aws.graphql.federation.common.exception.EntityDuplicateException
import com.github.arhor.aws.graphql.federation.common.exception.Operation
import com.github.arhor.dgs.users.data.entity.UserEntity
import com.github.arhor.dgs.users.data.repository.UserRepository
import com.github.arhor.dgs.users.generated.graphql.DgsConstants.USER
import com.github.arhor.dgs.users.generated.graphql.types.CreateUserInput
import com.github.arhor.dgs.users.generated.graphql.types.DeleteUserInput
import com.github.arhor.dgs.users.generated.graphql.types.UpdateUserInput
import com.github.arhor.dgs.users.generated.graphql.types.User
import com.github.arhor.dgs.users.service.events.UserEventEmitter
import com.github.arhor.dgs.users.service.mapping.UserMapper
import com.ninjasquad.springmockk.MockkBean
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
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.ComponentScan.Filter
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig

@SpringJUnitConfig
internal class UserServiceTest {

    @Configuration
    @ComponentScan(
        useDefaultFilters = false, includeFilters = [
            Filter(type = ASSIGNABLE_TYPE, classes = [UserService::class])
        ]
    )
    class Config

    @MockkBean
    private lateinit var mockUserMapper: UserMapper

    @MockkBean
    private lateinit var mockUserRepository: UserRepository

    @MockkBean
    private lateinit var mockUserEventEmitter: UserEventEmitter

    @MockkBean
    private lateinit var mockPasswordEncoder: PasswordEncoder

    @Autowired
    private lateinit var userService: UserService

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

            every { mockUserRepository.existsByUsername(any()) } returns false
            every { mockPasswordEncoder.encode(any()) } answers { firstArg() }
            every { mockUserMapper.mapToEntity(any()) } answers convertingDtoToUser
            every { mockUserRepository.save(any()) } answers copyingUserWithAssignedId(id = expectedId)
            every { mockUserMapper.mapToResult(any()) } answers convertingUserToDto

            // When
            val result = userService.createUser(input)

            // Then
            assertThat(result.user)
                .returns(expectedId, from { it.id })
                .returns(expectedUsername, from { it.username })

            verify(exactly = 1) { mockUserRepository.existsByUsername(any()) }
            verify(exactly = 1) { mockUserMapper.mapToEntity(any()) }
            verify(exactly = 1) { mockUserRepository.save(any()) }
            verify(exactly = 1) { mockUserMapper.mapToResult(any()) }
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

            every { mockUserRepository.existsByUsername(capture(username)) } returns true

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

            every { mockUserRepository.findByIdOrNull(any()) } returns user
            every { mockUserRepository.save(any()) } answers { firstArg() }
            every { mockUserMapper.mapToResult(any()) } answers convertingUserToDto
            every { mockPasswordEncoder.encode(any()) } answers { firstArg() }

            // When
            userService.updateUser(
                input = UpdateUserInput(
                    id = user.id!!,
                    password = "${user.password}-updated",
                )
            )

            // Then
            verify(exactly = 1) { mockUserRepository.save(any()) }
        }

        @Test
        fun `should not call save method on repository when there are no changes in user state`() {
            // Given
            val user = UserEntity(
                id = 1,
                username = "test-username",
                password = "test-password",
            )

            every { mockUserRepository.findByIdOrNull(any()) } returns user
            every { mockUserRepository.save(any()) } answers { firstArg() }
            every { mockUserMapper.mapToResult(any()) } answers convertingUserToDto
            every { mockPasswordEncoder.encode(any()) } answers { firstArg() }

            // When
            userService.updateUser(
                input = UpdateUserInput(
                    id = user.id!!,
                    password = user.password,
                )
            )

            // Then
            verify(exactly = 0) { mockUserRepository.save(any()) }
        }
    }

    @Nested
    inner class `UserService # deleteUser` {

        @Test
        fun `should return expected result deleting user`() {
            // Given
            val expectedId = 1L

            every { mockUserRepository.findByIdOrNull(any()) } returns mockk { every { id } returns expectedId }
            every { mockUserRepository.delete(any()) } just runs
            every { mockUserEventEmitter.emit(any()) } just runs

            // When
            userService.deleteUser(DeleteUserInput(expectedId))

            // Then
            verify(exactly = 1) { mockUserRepository.findByIdOrNull(any()) }
            verify(exactly = 1) { mockUserRepository.delete(any()) }
            verify(exactly = 1) { mockUserEventEmitter.emit(any()) }

            confirmVerified(mockUserMapper, mockUserRepository, mockUserEventEmitter, mockPasswordEncoder)
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
