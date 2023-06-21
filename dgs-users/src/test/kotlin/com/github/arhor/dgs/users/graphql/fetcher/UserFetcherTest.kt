package com.github.arhor.dgs.users.graphql.fetcher

import com.github.arhor.dgs.lib.exception.EntityNotFoundException
import com.github.arhor.dgs.lib.exception.Operation
import com.github.arhor.dgs.users.generated.graphql.DgsConstants.QUERY
import com.github.arhor.dgs.users.generated.graphql.DgsConstants.USER
import com.github.arhor.dgs.users.generated.graphql.types.Setting
import com.github.arhor.dgs.users.generated.graphql.types.User
import com.github.arhor.dgs.users.graphql.exception.CustomDataFetchingExceptionHandler
import com.github.arhor.dgs.users.service.UserService
import com.netflix.graphql.dgs.DgsQueryExecutor
import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration
import com.netflix.graphql.dgs.autoconfig.DgsExtendedScalarsAutoConfiguration
import com.ninjasquad.springmockk.MockkBean
import graphql.GraphQLError
import io.mockk.every
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.from
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(
    classes = [
        DgsAutoConfiguration::class,
        DgsExtendedScalarsAutoConfiguration::class,
        UserFetcher::class,
        CustomDataFetchingExceptionHandler::class,
    ]
)
internal class UserFetcherTest {

    @MockkBean
    private lateinit var userService: UserService

    @Autowired
    private lateinit var dgsQueryExecutor: DgsQueryExecutor

    @Test
    fun `should return successful result containing empty list of users`() {
        // given
        every { userService.getAllUsers(limit = any(), offset = any()) } returns emptyList()

        // when
        val result = dgsQueryExecutor.execute(
            """
            query {
                users(limit: 10, offset: 0) {
                    id
                    username
                    settings
                }
            }
            """
        )

        // then
        verify { userService.getAllUsers(limit = 10, offset = 0) }

        assertThat(result.errors)
            .isEmpty()
        assertThat(result.isDataPresent)
            .isTrue
        assertThat(result.getData<Map<String, *>>())
            .containsEntry("users", emptyList<Any>())
    }

    @Test
    fun `should return successful result containing list with single expected user`() {
        // given
        val user = User(
            id = "1",
            username = "test-user",
            settings = Setting.values().toList(),
        )

        every { userService.getAllUsers(limit = any(), offset = any()) } returns listOf(user)

        // when
        val result = dgsQueryExecutor.execute(
            """
            query {
                users(limit: 10, offset: 0) {
                    id
                    username
                    settings
                }
            }
            """
        )

        // then
        verify { userService.getAllUsers(limit = 10, offset = 0) }

        assertThat(result.errors)
            .isEmpty()
        assertThat(result.isDataPresent)
            .isTrue
        assertThat(result.getData<Map<Any, Any>>())
            .containsEntry(
                "users", listOf(
                    mapOf(
                        "id" to user.id,
                        "username" to user.username,
                        "settings" to user.settings?.map { it.name },
                    )
                )
            )
    }

    @Test
    fun `should return expected user by username without any exceptions`() {
        // Given
        val id = "1"
        val username = "test-username"

        val expectedErrors = emptyList<GraphQLError>()
        val expectedPresent = true
        val expectedData =
            mapOf(
                QUERY.User to mapOf(
                    USER.Id to id,
                    USER.Username to username
                )
            )

        every { userService.getUserByUsername(username = any()) } answers { User(id = id, username = firstArg()) }

        // When
        val result = dgsQueryExecutor.execute(
            """
            query (${'$'}username: String) {
                user(username: ${'$'}username) {
                    id
                    username
                }
            }
            """,
            mapOf(USER.Username to username)
        )

        // Then
        assertThat(result)
            .returns(expectedErrors, from { it.errors })
            .returns(expectedPresent, from { it.isDataPresent })
            .returns(expectedData, from { it.getData<Any>() })
    }

    @Test
    fun `should return GQL error trying to find user by incorrerct username`() {
        // Given
        val username = "test-username"

        every { userService.getUserByUsername(username = any()) } answers {
            throw EntityNotFoundException(
                entity = USER.TYPE_NAME,
                condition = "${USER.Username} = ${firstArg<String>()}",
                operation = Operation.READ,
            )
        }

        // When
        val result = dgsQueryExecutor.execute(
            """
            query (${'$'}username: String) {
                user(username: ${'$'}username) {
                    id
                    username
                }
            }
            """,
            mapOf(USER.Username to username)
        )

        // Then
        assertThat(result.errors)
            .singleElement()
            .returns(listOf(QUERY.User), from { it.path })
    }
}
