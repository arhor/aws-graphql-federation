package com.github.arhor.dgs.users.api.graphql.datafetcher

import com.github.arhor.dgs.lib.exception.EntityNotFoundException
import com.github.arhor.dgs.lib.exception.Operation
import com.github.arhor.dgs.users.api.graphql.GlobalDataFetchingExceptionHandler
import com.github.arhor.dgs.users.generated.graphql.DgsConstants.QUERY
import com.github.arhor.dgs.users.generated.graphql.DgsConstants.USER
import com.github.arhor.dgs.users.generated.graphql.types.User
import com.github.arhor.dgs.users.generated.graphql.types.UsersLookupInput
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
        GlobalDataFetchingExceptionHandler::class,
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
        every { userService.getAllUsers(any()) } returns emptyList()

        // when
        val result = dgsQueryExecutor.execute(
            """
            query {
                users {
                    id
                    username
                }
            }
            """
        )

        // then
        verify { userService.getAllUsers(input = UsersLookupInput(page = 0, size = 20)) }

        assertThat(result.errors)
            .isEmpty()
        assertThat(result.isDataPresent)
            .isTrue
        assertThat(result.getData<Map<String, *>>())
            .containsEntry(QUERY.Users, emptyList<Any>())
    }

    @Test
    fun `should return successful result containing list with single expected user`() {
        // given
        val user = User(
            id = 1,
            username = "test-user",
        )

        every { userService.getAllUsers(any()) } returns listOf(user)

        // when
        val result = dgsQueryExecutor.execute(
            """
            query {
                users(input: { page: 0, size: 10 }) {
                    id
                    username
                }
            }
            """
        )

        // then
        verify { userService.getAllUsers(input = UsersLookupInput(page = 0, size = 10)) }

        assertThat(result.errors)
            .isEmpty()
        assertThat(result.isDataPresent)
            .isTrue
        assertThat(result.getData<Map<Any, Any>>())
            .containsEntry(
                QUERY.Users, listOf(
                    mapOf(
                        USER.Id to user.id,
                        USER.Username to user.username,
                    )
                )
            )
    }

    @Test
    fun `should return expected user by username without any exceptions`() {
        // Given
        val id = 1L
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

        every { userService.getUserById(any()) } answers { User(id = firstArg(), username = username) }

        // When
        val result = dgsQueryExecutor.execute(
            """
            query (${'$'}id: Long!) {
                user(id: ${'$'}id) {
                    id
                    username
                }
            }
            """,
            mapOf(USER.Id to id)
        )

        // Then
        assertThat(result)
            .returns(expectedErrors, from { it.errors })
            .returns(expectedPresent, from { it.isDataPresent })
            .returns(expectedData, from { it.getData<Any>() })
    }

    @Test
    fun `should return GQL error trying to find user by incorrect username`() {
        // Given
        val id = 1L

        every { userService.getUserById(any()) } answers {
            throw EntityNotFoundException(
                entity = USER.TYPE_NAME,
                condition = "${USER.Id} = ${firstArg<Long>()}",
                operation = Operation.READ,
            )
        }

        // When
        val result = dgsQueryExecutor.execute(
            """
            query (${'$'}id: Long!) {
                user(id: ${'$'}id) {
                    id
                    username
                }
            }
            """,
            mapOf(USER.Id to id)
        )

        // Then
        assertThat(result.errors)
            .singleElement()
            .returns(listOf(QUERY.User), from { it.path })
    }
}
