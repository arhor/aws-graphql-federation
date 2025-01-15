package com.github.arhor.aws.graphql.federation.users.service.impl

import com.github.arhor.aws.graphql.federation.common.exception.EntityDuplicateException
import com.github.arhor.aws.graphql.federation.common.exception.EntityNotFoundException
import com.github.arhor.aws.graphql.federation.common.exception.Operation
import com.github.arhor.aws.graphql.federation.starter.security.CurrentUserRequest
import com.github.arhor.aws.graphql.federation.starter.testing.ZERO_UUID_VAL
import com.github.arhor.aws.graphql.federation.users.data.model.AuthEntity
import com.github.arhor.aws.graphql.federation.users.data.model.AuthRef
import com.github.arhor.aws.graphql.federation.users.data.model.UserEntity
import com.github.arhor.aws.graphql.federation.users.data.repository.AuthRepository
import com.github.arhor.aws.graphql.federation.users.data.repository.UserRepository
import com.github.arhor.aws.graphql.federation.users.generated.graphql.DgsConstants.USER
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.CreateUserInput
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.DeleteUserInput
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.UpdateUserInput
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.User
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.UsersLookupInput
import com.github.arhor.aws.graphql.federation.users.service.mapping.UserMapper
import io.mockk.Call
import io.mockk.MockKAnswerScope
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.assertj.core.api.Assertions.from
import org.assertj.core.api.InstanceOfAssertFactories.throwable
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.Optional
import java.util.UUID

class UserServiceImplTest {

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

    @AfterEach
    fun `confirm that all interactions with mocked dependencies were verified`() {
        confirmVerified(
            userMapper,
            userRepository,
            authRepository,
            eventPublisher,
            passwordEncoder,
        )
    }

    @Nested
    @DisplayName("Method getUserById")
    inner class GetUserByIdTest {
        @Test
        fun `should return an existing user by id`() {
            // Given
            val userEntity = mockk<UserEntity>()
            val userResult = mockk<User>()

            every { userRepository.findByIdOrNull(any()) } returns userEntity
            every { userMapper.mapToResult(any()) } returns userResult

            // When
            val result = userService.getUserById(USER_ID)

            // Then
            verify(exactly = 1) { userRepository.findById(USER_ID) }
            verify(exactly = 1) { userMapper.mapToResult(userEntity) }

            assertThat(result)
                .isNotNull()
                .isEqualTo(userResult)
        }

        @Test
        fun `should throw EntityNotFoundException trying to get non-existing user by id`() {
            // Given
            val expectedEntity = USER.TYPE_NAME
            val expectedOperation = Operation.LOOKUP
            val expectedCondition = "${USER.Id} = $USER_ID"
            val expectedExceptionType = EntityNotFoundException::class.java

            every { userRepository.findByIdOrNull(any()) } returns null

            // When
            val result = catchThrowable { userService.getUserById(USER_ID) }

            // Then
            verify(exactly = 1) { userRepository.findById(USER_ID) }

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
    @DisplayName("Method getAllUsers")
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
            val result = userService.getUserPage(input)

            // Then
            verify(exactly = 1) { userRepository.findAll(paged) }
            verify(exactly = 3) { userMapper.mapToResult(userEntity) }

            assertThat(result.data)
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
            val result = userService.getUserPage(input)

            // Then
            verify(exactly = 1) { userRepository.findAll(paged) }

            assertThat(result.data)
                .isNotNull()
                .isEmpty()
        }
    }

    @Nested
    @DisplayName("Method getUserByUsernameAndPassword")
    inner class GetUserByUsernameAndPasswordTest {
        @Test
        fun `should return current user for a valid pair of username and password`() {
            // Given
            val request = CurrentUserRequest(username = "test-username", password = "test-password")
            val entity = mockk<UserEntity>()
            val expectedAuthorities = emptyList<AuthEntity>()
            val expectedAuthorityRefs = emptySet<AuthRef>()

            every { entity.username } returns request.username
            every { entity.password } returns request.password
            every { entity.authorities } returns expectedAuthorityRefs
            every { userRepository.findByUsername(any()) } returns entity
            every { passwordEncoder.matches(any(), any()) } returns true
            every { authRepository.findAllById(any()) } returns expectedAuthorities
            every { userMapper.mapToCurrentUser(any(), any()) } returns mockk()

            // When
            val user = userService.getUserByUsernameAndPassword(request)

            verify(exactly = 1) { userRepository.findByUsername(request.username) }
            verify(exactly = 1) { passwordEncoder.matches(request.password, request.password) }
            verify(exactly = 1) { authRepository.findAllById(expectedAuthorityRefs.map { it.authId }) }
            verify(exactly = 1) { userMapper.mapToCurrentUser(entity, expectedAuthorities) }

            // Then
            assertThat(user)
                .isNotNull()
        }

        @Test
        fun `should throw UsernameNotFoundException exception trying to get current user with invalid username`() {
            // Given
            val request = CurrentUserRequest(username = "test-username", password = "test-password")

            every { userRepository.findByUsername(any()) } returns null

            // When
            val result = catchThrowable { userService.getUserByUsernameAndPassword(request) }

            verify(exactly = 1) { userRepository.findByUsername(request.username) }

            // Then
            assertThat(result)
                .isNotNull()
                .asInstanceOf(throwable(UsernameNotFoundException::class.java))
                .hasMessage("Bad Credentials")
        }

        @Test
        fun `should throw UsernameNotFoundException exception trying to get current user with invalid password`() {
            // Given
            val request = CurrentUserRequest(username = "test-username", password = "test-password")
            val entity = mockk<UserEntity>()

            every { entity.id } returns USER_ID
            every { entity.username } returns request.username
            every { entity.password } returns request.password
            every { userRepository.findByUsername(any()) } returns entity
            every { passwordEncoder.matches(any(), any()) } returns false

            // When
            val result = catchThrowable { userService.getUserByUsernameAndPassword(request) }

            verify(exactly = 1) { userRepository.findByUsername(request.username) }
            verify(exactly = 1) { passwordEncoder.matches(request.password, request.password) }

            // Then
            assertThat(result)
                .isNotNull()
                .asInstanceOf(throwable(UsernameNotFoundException::class.java))
                .hasMessage("Bad Credentials")
        }
    }

    @Nested
    @DisplayName("Method createUser")
    inner class CreateUserTest {
        @Test
        fun `should correctly create new user entity and return DTO with assigned id`() {
            // Given
            val expectedUsername = "test@email.com"
            val expectedPassword = "TestPassword123"

            val input = CreateUserInput(
                username = expectedUsername,
                password = expectedPassword,
            )

            every { userRepository.existsByUsername(any()) } returns false
            every { authRepository.findByName(any()) } returns mockk()
            every { passwordEncoder.encode(any()) } answers { firstArg() }
            every { userMapper.mapToEntity(any(), any()) } answers convertingDtoToUser
            every { userRepository.save(any()) } answers copyingUserWithAssignedId(id = USER_ID)
            every { eventPublisher.publishEvent(any<Any>()) } just runs
            every { userMapper.mapToResult(any()) } answers convertingUserToDto

            // When
            val result = userService.createUser(input)

            // Then
            assertThat(result)
                .returns(USER_ID, from { it.id })
                .returns(expectedUsername, from { it.username })

            verify(exactly = 1) { userRepository.existsByUsername(any()) }
            verify(exactly = 1) { authRepository.findByName(any()) }
            verify(exactly = 1) { passwordEncoder.encode(any()) }
            verify(exactly = 1) { userMapper.mapToEntity(any(), any()) }
            verify(exactly = 1) { userRepository.save(any()) }
            verify(exactly = 1) { eventPublisher.publishEvent(any<Any>()) }
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

            every { userRepository.existsByUsername(any()) } returns true

            // When
            val result = catchThrowable { userService.createUser(input) }

            verify(exactly = 1) { userRepository.existsByUsername(input.username) }

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

    @Nested
    @DisplayName("Method updateUser")
    inner class UpdateUserTest {
        @Test
        fun `should save updated user state to repository when there are actual changes`() {
            // Given
            val user = UserEntity(
                id = USER_ID,
                username = "test-username",
                password = "test-password",
            )

            every { userRepository.findById(any()) } returns Optional.of(user)
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
            verify(exactly = 1) { userRepository.findById(any()) }
            verify(exactly = 1) { userRepository.save(any()) }
            verify(exactly = 1) { userMapper.mapToResult(any()) }
            verify(exactly = 1) { passwordEncoder.encode(any()) }
        }

        @Test
        fun `should not call save method on repository when there are no changes in user state`() {
            // Given
            val user = UserEntity(
                id = USER_ID,
                username = "test-username",
                password = "test-password",
            )

            every { userRepository.findById(any()) } returns Optional.of(user)
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
            verify(exactly = 1) { userRepository.findById(any()) }
            verify(exactly = 1) { userMapper.mapToResult(any()) }
            verify(exactly = 1) { passwordEncoder.encode(any()) }
        }
    }

    @Nested
    @DisplayName("Method deleteUser")
    inner class DeleteUserTest {
        @Test
        fun `should return expected result deleting user`() {
            // Given
            every { userRepository.findByIdOrNull(any()) } returns mockk { every { id } returns USER_ID }
            every { userRepository.delete(any()) } just runs
            every { eventPublisher.publishEvent(any<Any>()) } just runs

            // When
            userService.deleteUser(DeleteUserInput(USER_ID))

            // Then
            verify(exactly = 1) { userRepository.findByIdOrNull(any()) }
            verify(exactly = 1) { userRepository.delete(any()) }
            verify(exactly = 1) { eventPublisher.publishEvent(any<Any>()) }
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

    companion object {
        private val USER_ID = ZERO_UUID_VAL
    }
}
