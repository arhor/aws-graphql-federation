@file:Suppress("ClassName")

package com.github.arhor.dgs.users.service

import com.github.arhor.dgs.users.data.entity.UserEntity
import com.github.arhor.dgs.users.generated.graphql.types.CreateUserInput
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchException
import org.assertj.core.api.Assertions.from
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.ComponentScan.Filter
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.FilterType
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig

@SpringJUnitConfig
internal class UserMapperTest {

    @Configuration
    @ComponentScan(
        useDefaultFilters = false, includeFilters = [
            Filter(type = FilterType.ASSIGNABLE_TYPE, classes = [UserMapper::class])
        ]
    )
    class Config

    @Autowired
    private lateinit var userMapper: UserMapper

    @Nested
    inner class `UserMapper # mapToEntity` {
        @Test
        fun `should return UserEntity instance with fields mapped from passed User instance`() {
            // Given
            val input = CreateUserInput(
                username = "test-username",
                password = "test-password",
            )

            // When
            val entity = userMapper.mapToEntity(input)

            // Then
            assertThat(entity)
                .returns(input.username, from { it.username })
                .returns(input.password, from { it.password })
        }
    }

    @Nested
    inner class `UserMapper # mapToDTO` {
        @Test
        fun `should return User instance with fields mapped from passed UserEntity instance`() {
            // Given
            val entity = UserEntity(
                id = -1L,
                username = "test-username",
                password = "test-password",
            )

            // When
            val user = userMapper.mapToDTO(entity)

            // Then
            assertThat(user)
                .returns(entity.id, from { it.id })
                .returns(entity.username, from { it.username })
        }

        @Test
        fun `should throw IllegalArgumentException trying to map UserEntity without assigned id`() {
            // Given
            val entity = UserEntity(
                username = "test-username",
                password = "test-password",
            )

            // When
            val exception = catchException { userMapper.mapToDTO(entity) }

            // Then
            assertThat(exception)
                .isInstanceOf(IllegalArgumentException::class.java)
        }
    }
}
