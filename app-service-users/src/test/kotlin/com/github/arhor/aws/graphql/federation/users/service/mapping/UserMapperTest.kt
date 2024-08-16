package com.github.arhor.aws.graphql.federation.users.service.mapping

import com.github.arhor.aws.graphql.federation.starter.testing.ZERO_UUID_VAL
import com.github.arhor.aws.graphql.federation.users.data.model.AuthEntity
import com.github.arhor.aws.graphql.federation.users.data.model.AuthRef
import com.github.arhor.aws.graphql.federation.users.data.model.UserEntity
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.CreateUserInput
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchException
import org.assertj.core.api.Assertions.catchThrowable
import org.assertj.core.api.Assertions.from
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.FilterType
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig

@SpringJUnitConfig
class UserMapperTest {

    @Configuration
    @ComponentScan(
        includeFilters = [
            ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = [UserMapper::class])
        ],
        useDefaultFilters = false,
    )
    class Config

    @Autowired
    private lateinit var userMapper: UserMapper

    @Nested
    @DisplayName("UserMapper :: mapToEntity")
    inner class MapToEntityTest {
        @Test
        fun `should map CreateUserInput instance to UserEntity without exceptions`() {
            // Given
            val expectedUsername = "test-username"
            val expectedPassword = "test-password"
            val expectedAuthRef = AuthRef(authId = 1)
            val expectedAuthorities = setOf(expectedAuthRef)

            val input = mockk<CreateUserInput>()
            val defaultAuthority = mockk<AuthEntity>()

            every { input.username } returns expectedUsername
            every { input.password } returns expectedPassword
            every { defaultAuthority.id } returns expectedAuthRef.authId

            // When
            val entity = userMapper.mapToEntity(input, defaultAuthority)

            // Then
            assertThat(entity)
                .returns(expectedUsername, from { it.username })
                .returns(expectedPassword, from { it.password })
                .returns(expectedAuthorities, from { it.authorities })
        }
    }

    @Nested
    @DisplayName("UserMapper :: mapToResult")
    inner class MapToResultTest {
        @Test
        fun `should map UserEntity instance to User without exceptions`() {
            // Given
            val expectedUsername = "test-username"
            val user = mockk<UserEntity>()

            every { user.id } returns USER_ID
            every { user.username } returns expectedUsername

            // When
            val result = userMapper.mapToResult(user)

            // Then
            assertThat(result)
                .returns(USER_ID, from { it.id })
                .returns(expectedUsername, from { it.username })
        }

        @Test
        fun `should throw IllegalArgumentException trying to map user without id`() {
            // Given
            val user = mockk<UserEntity>()

            every { user.id } returns null

            // When
            val result = catchThrowable { userMapper.mapToResult(user) }

            // Then
            assertThat(result)
                .isInstanceOf(IllegalArgumentException::class.java)
        }
    }

    @Nested
    @DisplayName("UserMapper :: mapToCurrentUser")
    inner class MapToCurrentUserTest {
        @Test
        fun `should correctly map user with authorities to CurrentUser object`() {
            // Given
            val user = mockk<UserEntity>()
            val auth = mockk<AuthEntity>()
            val authName = "test-auth"

            every { user.id } returns USER_ID
            every { auth.name } returns authName

            // When
            val result = userMapper.mapToCurrentUser(user, listOf(auth))

            // Then
            assertThat(result)
                .isNotNull()
                .returns(USER_ID, from { it.id })
                .returns(listOf(authName), from { it.authorities })
        }

        @Test
        fun `should throw IllegalArgumentException trying to map user without ID`() {
            // Given
            val user = mockk<UserEntity>()
            val auth = mockk<AuthEntity>()

            every { user.id } returns null

            // When
            val result = catchException { userMapper.mapToCurrentUser(user, listOf(auth)) }

            // Then
            assertThat(result)
                .isNotNull()
                .isInstanceOf(IllegalArgumentException::class.java)
        }
    }

    companion object {
        private val USER_ID = ZERO_UUID_VAL
    }
}
