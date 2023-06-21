@file:Suppress("ClassName", "SameParameterValue")

package com.github.arhor.dgs.users.service

import com.github.arhor.dgs.lib.exception.EntityDuplicateException
import com.github.arhor.dgs.lib.exception.Operation
import com.github.arhor.dgs.users.data.entity.UserEntity
import com.github.arhor.dgs.users.data.repository.UserRepository
import com.github.arhor.dgs.users.generated.graphql.DgsConstants.USER
import com.github.arhor.dgs.users.generated.graphql.types.CreateUserInput
import com.github.arhor.dgs.users.generated.graphql.types.Setting
import com.github.arhor.dgs.users.generated.graphql.types.User
import com.github.arhor.dgs.users.service.impl.UserServiceImpl
import io.mockk.Call
import io.mockk.MockKAnswerScope
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchException
import org.assertj.core.api.Assertions.from
import org.assertj.core.api.InstanceOfAssertFactories.throwable
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.EnumSet
import java.util.stream.Stream

internal class UserServiceTest {

    private val mockkUserMapper: UserMapper = mockk()
    private val mockkUserRepository: UserRepository = mockk()
    private val mockkPasswordEncoder: PasswordEncoder = mockk()

    private val userService: UserService = UserServiceImpl(
        mockkUserMapper,
        mockkUserRepository,
        mockkPasswordEncoder,
    )

    @Nested
    inner class `UserService # createUser` {
        @Test
        fun `should correctly create new user entity and return DTO with assigned id`() {
            // Given
            val expectedId = 1L
            val expectedUsername = "test@email.com"
            val expectedPassword = "TestPassword123"
            val expectedSettings = emptyList<String>()

            val input = CreateUserInput(
                username = expectedUsername,
                password = expectedPassword,
            )

            every { mockkUserRepository.existsByUsername(any()) } returns false
            every { mockkPasswordEncoder.encode(any()) } answers { firstArg() }
            every { mockkUserMapper.mapToEntity(any()) } answers convertingDtoToUser()
            every { mockkUserRepository.save(any()) } answers copyingUserWithAssignedId(id = expectedId)
            every { mockkUserMapper.mapToDTO(any()) } answers convertingUserToDto()

            // When
            val result = userService.createUser(input)

            // Then
            assertThat(result)
                .returns(expectedId, from { it.id.toLong() })
                .returns(expectedUsername, from { it.username })
                .returns(expectedSettings, from { it.settings })

            verify(exactly = 1) { mockkUserRepository.existsByUsername(any()) }
            verify(exactly = 1) { mockkUserMapper.mapToEntity(any()) }
            verify(exactly = 1) { mockkUserRepository.save(any()) }
            verify(exactly = 1) { mockkUserMapper.mapToDTO(any()) }
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

            every { mockkUserRepository.existsByUsername(capture(username)) } returns true

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
    inner class `UserService # deleteUser` {

        @MethodSource("com.github.arhor.dgs.users.service.UserServiceTest#delete user positive test data factory")
        @ParameterizedTest
        fun `should return expected result deleting user`(
            // Given
            affectedRowNum: Int,
            expectedResult: Boolean,
        ) {
            val expectedId = 1L

            every { mockkUserRepository.deleteByIdReturningNumberRecordsAffected(any()) } returns affectedRowNum

            // When
            val result = userService.deleteUser(expectedId)

            // Then
            assertThat(result)
                .isEqualTo(expectedResult)

            verify(exactly = 1) { mockkUserRepository.deleteByIdReturningNumberRecordsAffected(expectedId) }

            confirmVerified(mockkUserMapper, mockkUserRepository, mockkPasswordEncoder)
        }

        @Test
        fun `should throw EntityDuplicateException trying to delete more then one user by id`() {
            // Given
            val id = 1L

            val expectedEntity = USER.TYPE_NAME
            val expectedOperation = Operation.DELETE
            val expectedCondition = "${USER.Id} = $id"
            val expectedExceptionType = EntityDuplicateException::class.java


            every { mockkUserRepository.deleteByIdReturningNumberRecordsAffected(any()) } returns 2

            // When
            val result = catchException { userService.deleteUser(id) }

            // Then
            assertThat(result)
                .asInstanceOf(throwable(expectedExceptionType))
                .satisfies(
                    { assertThat(it.entity).describedAs("entity").isEqualTo(expectedEntity) },
                    { assertThat(it.operation).describedAs("operation").isEqualTo(expectedOperation) },
                    { assertThat(it.condition).describedAs("condition").isEqualTo(expectedCondition) },
                )
        }
    }

    private fun convertingDtoToUser(): MockKAnswerScope<UserEntity, *>.(Call) -> UserEntity = {
        firstArg<CreateUserInput>().let {

            UserEntity(
                username = it.username,
                password = it.password,
                settings = EnumSet.noneOf(Setting::class.java).apply { addAll(it.settings ?: emptyList()) },
            )
        }
    }

    private fun copyingUserWithAssignedId(id: Long): MockKAnswerScope<UserEntity, *>.(Call) -> UserEntity = {
        firstArg<UserEntity>().copy(id = id)
    }

    private fun convertingUserToDto(): MockKAnswerScope<User, *>.(Call) -> User = {
        firstArg<UserEntity>().let {
            User(
                id = it.id!!,
                username = it.username,
                settings = it.settings.toList(),
            )
        }
    }

    companion object {
        @JvmStatic
        fun `delete user positive test data factory`(): Stream<Arguments> =
            Stream.of(
                arguments(1, true),
                arguments(0, false),
            )
    }
}
