package com.github.arhor.dgs.users.service

import com.github.arhor.dgs.users.data.entity.Setting
import com.github.arhor.dgs.users.data.entity.UserEntity
import com.github.arhor.dgs.users.data.repository.UserRepository
import com.github.arhor.dgs.users.generated.graphql.types.CreateUserRequest
import com.github.arhor.dgs.users.generated.graphql.types.User
import com.github.arhor.dgs.users.service.impl.UserServiceImpl
import io.mockk.Call
import io.mockk.MockKAnswerScope
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.from
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.security.crypto.password.PasswordEncoder

@Suppress("ClassName")
internal class UserServiceTest {

    private val userMapper: UserMapper = mockk()
    private val userRepository: UserRepository = mockk()
    private val passwordEncoder: PasswordEncoder = mockk()

    private val userService: UserService = UserServiceImpl(
        userMapper,
        userRepository,
        passwordEncoder,
    )

    @Nested
    inner class createUser {
        @Test
        fun `should correctly save new user and return it with assigned id`() {
            // Given
            val expectedId = 1L
            val expectedUsername = "test@email.com"
            val expectedPassword = "TestPassword123"
            val expectedSettings = Setting.emptySet()

            val userCreateRequest = CreateUserRequest(
                username = expectedUsername,
                password = expectedPassword,
            )

            every { userRepository.existsByUsername(any()) } returns false
            every { passwordEncoder.encode(any()) } answers { firstArg() }
            every { userMapper.mapToEntity(any()) } answers convertingDtoToUser()
            every { userRepository.save(any()) } answers copyingUserWithAssignedId(id = expectedId)
            every { userMapper.mapToDTO(any()) } answers convertingUserToDto()

            // When
            val result = userService.createUser(userCreateRequest)

            // Then
            assertThat(result)
                .returns(expectedId, from { it.id.toLong() })
                .returns(expectedUsername, from { it.username })
                .returns(expectedSettings, from { it.settings })

            verify(exactly = 1) { userRepository.existsByUsername(any()) }
            verify(exactly = 1) { userMapper.mapToEntity(any()) }
            verify(exactly = 1) { userRepository.save(any()) }
            verify(exactly = 1) { userMapper.mapToDTO(any()) }
        }

//        @Test
//        fun `should throw EntityDuplicateException creating user with already taken email`() {
//            // Given
//            val userCreateRequest = UserCreateRequestDto(
//                email = "test@email.com",
//                password = "TestPassword123",
//                budgetLimit = BigDecimal("150.00")
//            )
//            val expectedEntity = "User"
//            val expectedOperation = "CREATE"
//            val expectedCondition = "email = ${userCreateRequest.email}"
//            val expectedExceptionType = EntityDuplicateException::class.java
//
//            every { userRepository.existsByEmail(any()) } returns true
//
//            // When
//            val result = catchException { userService.createUser(userCreateRequest) }
//
//            // Then
//            assertThat(result)
//                .isInstanceOf(expectedExceptionType)
//                .asInstanceOf(throwable(expectedExceptionType))
//                .satisfies(
//                    { assertThat(it.entity).describedAs("entity").isEqualTo(expectedEntity) },
//                    { assertThat(it.operation).describedAs("operation").isEqualTo(expectedOperation) },
//                    { assertThat(it.condition).describedAs("condition").isEqualTo(expectedCondition) },
//                )
//
//            verify(exactly = 1) { userRepository.existsByEmail(userCreateRequest.email) }
//        }
    }

    @Nested
    inner class deleteUser {

        @Test
        fun `should correctly delete an existing user also emitting corresponding event`() {
            // Given

            // When

            // Then

        }

        @Test
        fun `should throw EntityNotFoundException updating a non-existing user`() {
            // Given

            // When

            // Then

        }
    }

    private fun convertingDtoToUser(): MockKAnswerScope<UserEntity, *>.(Call) -> UserEntity = {
        firstArg<CreateUserRequest>().let {
            UserEntity(
                username = it.username,
                password = it.password,
                settings = it.settings ?: Setting.emptySet(),
            )
        }
    }

    private fun copyingUser(): MockKAnswerScope<UserEntity, *>.(Call) -> UserEntity = {
        firstArg<UserEntity>().copy()
    }

    private fun copyingUserWithAssignedId(id: Long): MockKAnswerScope<UserEntity, *>.(Call) -> UserEntity = {
        firstArg<UserEntity>().copy(id = id)
    }

    private fun convertingUserToDto(): MockKAnswerScope<User, *>.(Call) -> User = {
        firstArg<UserEntity>().let {
            User(
                id = it.id.toString(),
                username = it.username,
                settings = it.settings,
            )
        }
    }
}
