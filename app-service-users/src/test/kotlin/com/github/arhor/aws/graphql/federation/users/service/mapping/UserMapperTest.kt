package com.github.arhor.aws.graphql.federation.users.service.mapping

import com.github.arhor.aws.graphql.federation.users.data.entity.UserEntity
import com.github.arhor.aws.graphql.federation.users.service.mapping.UserMapper
import com.github.arhor.dgs.users.generated.graphql.types.CreateUserInput
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchException
import org.assertj.core.api.Assertions.from
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.FilterType
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig

@SpringJUnitConfig
internal class UserMapperTest {

    @Configuration
    @ComponentScan(
        includeFilters = [
            ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = [UserMapper::class])
        ],
        useDefaultFilters = false,
    )
    class Config

    @Autowired
    private lateinit var userMapperUnderTest: UserMapper

    @Test
    fun `should map CreateUserInput instance to UserEntity without exceptions`() {
        // Given
        val expectedUsername = "test-username"
        val expectedPassword = "test-password"

        val input = mockk<CreateUserInput>()

        every { input.username } returns expectedUsername
        every { input.password } returns expectedPassword

        // When
        val entity = userMapperUnderTest.mapToEntity(input)

        // Then
        assertThat(entity)
            .returns(expectedUsername, from { it.username })
            .returns(expectedPassword, from { it.password })
    }

    @Test
    fun `should map UserEntity instance to User without exceptions`() {
        // Given
        val expectedId = -1L
        val expectedUsername = "test-username"

        val entity = mockk<UserEntity>()

        every { entity.id } returns expectedId
        every { entity.username } returns expectedUsername

        // When
        val result = userMapperUnderTest.mapToResult(entity)

        // Then
        assertThat(result)
            .returns(expectedId, from { it.id })
            .returns(expectedUsername, from { it.username })
    }

    @Test
    fun `should throw IllegalArgumentException trying to map UserEntity without id`() {
        // Given
        val entity = mockk<UserEntity>()

        every { entity.id } returns null

        // When
        val result = catchException { userMapperUnderTest.mapToResult(entity) }

        // Then
        assertThat(result)
            .isInstanceOf(IllegalArgumentException::class.java)
    }
}
