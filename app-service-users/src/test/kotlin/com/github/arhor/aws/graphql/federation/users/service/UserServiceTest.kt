@file:Suppress("SameParameterValue")

package com.github.arhor.aws.graphql.federation.users.service

import com.github.arhor.aws.graphql.federation.common.exception.EntityDuplicateException
import com.github.arhor.aws.graphql.federation.common.exception.EntityNotFoundException
import com.github.arhor.aws.graphql.federation.common.exception.Operation
import com.github.arhor.aws.graphql.federation.security.CurrentUserRequest
import com.github.arhor.aws.graphql.federation.users.data.entity.UserEntity
import com.github.arhor.aws.graphql.federation.users.data.repository.AuthRepository
import com.github.arhor.aws.graphql.federation.users.data.repository.UserRepository
import com.github.arhor.aws.graphql.federation.users.generated.graphql.DgsConstants.USER
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.CreateUserInput
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.DeleteUserInput
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.UpdateUserInput
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.User
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.UsersLookupInput
import com.github.arhor.aws.graphql.federation.users.service.impl.UserServiceImpl
import com.github.arhor.aws.graphql.federation.users.service.mapping.UserMapper
import com.netflix.graphql.dgs.exceptions.DgsBadRequestException
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
import org.assertj.core.api.Assertions.catchThrowable
import org.assertj.core.api.Assertions.from
import org.assertj.core.api.InstanceOfAssertFactories.throwable
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.UUID

internal class UserServiceTest {

    private val userMapper: UserMapper = mockk()
    private val userRepository: UserRepository = mockk()
    private val authRepository: AuthRepository = mockk()
    private val eventPublisher: ApplicationEventPublisher = mockk()
    private val passwordEncoder: PasswordEncoder = mockk()

    private val userService = UserServiceImpl(
        userMapper,
        userRepository,
        authRepository,
        eventPublisher,
        passwordEncoder,
    )

    @Nested
    @DisplayName("UserService :: getUserById")
    inner class GetUserByIdTest {

        @Test
        fun `should return an existing user by id`() {
            // Given
            val userId = UUID.randomUUID()
            val userEntity = mockk<UserEntity>()
            val userResult = mockk<User>()

            every { userRepository.findByIdOrNull(any()) } returns userEntity
            every { userMapper.mapToResult(any()) } returns userResult

            // When
            val result = userService.getUserById(userId)

            // Then
            verify(exactly = 1) { userRepository.findById(userId) }
            verify(exactly = 1) { userMapper.mapToResult(userEntity) }

            confirmVerified(userRepository, userMapper)

            assertThat(result)
                .isNotNull()
                .isEqualTo(userResult)
        }

        @Test
        fun `should throw EntityNotFoundException trying to get non-existing user by id`() {
            // Given
            val userId = UUID.randomUUID()

            val expectedEntity = USER.TYPE_NAME
            val expectedOperation = Operation.LOOKUP
            val expectedCondition = "${USER.Id} = $userId"
            val expectedExceptionType = EntityNotFoundException::class.java

            every { userRepository.findByIdOrNull(any()) } returns null

            // When
            val result = catchThrowable { userService.getUserById(userId) }

            // Then
            verify(exactly = 1) { userRepository.findById(userId) }

            confirmVerified(userRepository, userMapper)

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
    @DisplayName("UserService :: getAllUsers")
    inner class GetAllUsersTest {

        @Test
        fun `should return an expected list of users`() {
            // Given
            val input = UsersLookupInput(page = 1, size = 3)
            val paged = Pageable.ofSize(input.size).withPage(input.page)
            val userEntity = mockk<UserEntity>()
            val resultList = List(input.size) { userEntity }

            every { userRepository.findAll(any<Pageable>()) } returns PageImpl(resultList)
            every { userMapper.mapToResult(any()) } returns mockk()

            // When
            val result = userService.getAllUsers(input)

            // Then
            verify(exactly = 1) { userRepository.findAll(paged) }
            verify(exactly = 3) { userMapper.mapToResult(userEntity) }

            confirmVerified(userRepository, userMapper)

            assertThat(result)
                .isNotNull()
                .hasSameSizeAs(resultList)
        }

        @Test
        fun `should return an empty list when there are no users found`() {
            // Given
            val input = UsersLookupInput(page = 1, size = 3)
            val paged = Pageable.ofSize(input.size).withPage(input.page)

            every { userRepository.findAll(any<Pageable>()) } returns Page.empty(paged)

            // When
            val result = userService.getAllUsers(input)

            // Then
            verify(exactly = 1) { userRepository.findAll(paged) }

            confirmVerified(userRepository, userMapper)

            assertThat(result)
                .isNotNull()
                .isEmpty()
        }
    }

    @Nested
    @DisplayName("UserService :: getUserByUsernameAndPassword")
    inner class GetUserByUsernameAndPasswordTest {

        @Test
        fun `should return current user for a valid pair of username and password`() {
            // Given
            val request = CurrentUserRequest(username = "test-username", password = "test-password")
            val userId = UUID.randomUUID()
            val entity = mockk<UserEntity>()

            every { entity.id } returns userId
            every { entity.username } returns request.username
            every { entity.password } returns request.password
            every { entity.authorities } returns emptySet()
            every { userRepository.findByUsername(any()) } returns entity
            every { passwordEncoder.matches(any(), any()) } returns true

            // When
            val user = userService.getUserByUsernameAndPassword(request)

            verify(exactly = 1) { userRepository.findByUsername(request.username) }
            verify(exactly = 1) { passwordEncoder.matches(request.password, request.password) }

            confirmVerified(userRepository, passwordEncoder)

            // Then
            assertThat(user)
                .isNotNull()
                .returns(userId, from { it.id })
                .returns(listOf("ROLE_USER"), from { it.authorities })
        }

        @Test
        fun `should throw DgsBadRequestException exception trying to get current user with invalid username`() {
            // Given
            val request = CurrentUserRequest(username = "test-username", password = "test-password")

            every { userRepository.findByUsername(any()) } returns null

            // When
            val result = catchThrowable { userService.getUserByUsernameAndPassword(request) }

            verify(exactly = 1) { userRepository.findByUsername(request.username) }

            confirmVerified(userRepository, passwordEncoder)

            // Then
            assertThat(result)
                .isNotNull()
                .asInstanceOf(throwable(DgsBadRequestException::class.java))
                .hasMessage("Bad Credentials")
        }

        @Test
        fun `should throw DgsBadRequestException exception trying to get current user with invalid password`() {
            // Given
            val request = CurrentUserRequest(username = "test-username", password = "test-password")
            val userId = UUID.randomUUID()
            val entity = mockk<UserEntity>()

            every { entity.id } returns userId
            every { entity.username } returns request.username
            every { entity.password } returns request.password
            every { userRepository.findByUsername(any()) } returns entity
            every { passwordEncoder.matches(any(), any()) } returns false

            // When
            val result = catchThrowable { userService.getUserByUsernameAndPassword(request) }

            verify(exactly = 1) { userRepository.findByUsername(request.username) }
            verify(exactly = 1) { passwordEncoder.matches(request.password, request.password) }

            confirmVerified(userRepository, passwordEncoder)

            // Then
            assertThat(result)
                .isNotNull()
                .asInstanceOf(throwable(DgsBadRequestException::class.java))
                .hasMessage("Bad Credentials")
        }
    }

    @Nested
    @DisplayName("UserService :: createUser")
    inner class CreateUserTest {
        @Test
        fun `should correctly create new user entity and return DTO with assigned id`() {
            // Given
            val expectedId = UUID.randomUUID()
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
            val result = catchThrowable { userService.createUser(input) }

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
    @DisplayName("UserService :: updateUser")
    inner class UpdateUserTest {

        @Test
        fun `should save updated user state to repository when there are actual changes`() {
            // Given
            val user = UserEntity(
                id = UUID.randomUUID(),
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
                id = UUID.randomUUID(),
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
    @DisplayName("UserService :: deleteUser")
    inner class DeleteUserTest {

        @Test
        fun `should return expected result deleting user`() {
            // Given
            val expectedId = UUID.randomUUID()

            every { userRepository.findByIdOrNull(any()) } returns mockk { every { id } returns expectedId }
            every { userRepository.delete(any()) } just runs
            every { eventPublisher.publishEvent(any<Any>()) } just runs

            // When
            userService.deleteUser(DeleteUserInput(expectedId))

            // Then
            verify(exactly = 1) { userRepository.findByIdOrNull(any()) }
            verify(exactly = 1) { userRepository.delete(any()) }
            verify(exactly = 1) { eventPublisher.publishEvent(any<Any>()) }

            confirmVerified(userMapper, userRepository, eventPublisher, passwordEncoder)
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

    private fun copyingUserWithAssignedId(id: UUID): MockKAnswerScope<UserEntity, *>.(Call) -> UserEntity = {
        firstArg<UserEntity>().copy(id = id)
    }
}
