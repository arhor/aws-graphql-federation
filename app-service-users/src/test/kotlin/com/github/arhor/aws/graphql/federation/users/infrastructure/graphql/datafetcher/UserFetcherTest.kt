package com.github.arhor.aws.graphql.federation.users.infrastructure.graphql.datafetcher

import com.github.arhor.aws.graphql.federation.common.exception.EntityNotFoundException
import com.github.arhor.aws.graphql.federation.common.exception.Operation
import com.github.arhor.aws.graphql.federation.security.ConfigureSecurity
import com.github.arhor.aws.graphql.federation.spring.dgs.GlobalDataFetchingExceptionHandler
import com.github.arhor.aws.graphql.federation.users.generated.graphql.DgsConstants.QUERY
import com.github.arhor.aws.graphql.federation.users.generated.graphql.DgsConstants.USER
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.CreateUserInput
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.DeleteUserInput
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.UpdateUserInput
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.User
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.UsersLookupInput
import com.github.arhor.aws.graphql.federation.users.service.UserService
import com.netflix.graphql.dgs.DgsQueryExecutor
import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration
import com.netflix.graphql.dgs.autoconfig.DgsExtendedScalarsAutoConfiguration
import com.ninjasquad.springmockk.MockkBean
import graphql.GraphQLError
import io.mockk.every
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.from
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.WithAnonymousUser
import org.springframework.security.test.context.support.WithMockUser
import java.util.UUID

@SpringBootTest(
    classes = [
        ConfigureSecurity::class,
        DgsAutoConfiguration::class,
        DgsExtendedScalarsAutoConfiguration::class,
        GlobalDataFetchingExceptionHandler::class,
        UserFetcher::class,
    ]
)
class UserFetcherTest {

    @MockkBean
    private lateinit var userService: UserService

    @Autowired
    private lateinit var dgsQueryExecutor: DgsQueryExecutor

    @Nested
    @DisplayName("query { user }")
    inner class UserQueryTest {
        @Test
        fun `should return expected user by username without any exceptions`() {
            // Given
            val expectedId = UUID.randomUUID()
            val expectedUsername = "test-username"

            val expectedErrors = emptyList<GraphQLError>()
            val expectedPresent = true
            val expectedData =
                mapOf(
                    QUERY.User to mapOf(
                        USER.Id to expectedId.toString(),
                        USER.Username to expectedUsername
                    )
                )

            every { userService.getUserById(any()) } answers { User(id = firstArg(), username = expectedUsername) }

            // When
            val result = dgsQueryExecutor.execute(
                """
                query (${'$'}id: UUID!) {
                    user(id: ${'$'}id) {
                        id
                        username
                    }
                }
                """,
                mapOf(USER.Id to expectedId)
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
            val id = UUID.randomUUID()

            every { userService.getUserById(any()) } answers {
                throw EntityNotFoundException(
                    entity = USER.TYPE_NAME,
                    condition = "${USER.Id} = ${firstArg<Long>()}",
                    operation = Operation.LOOKUP,
                )
            }

            // When
            val result = dgsQueryExecutor.execute(
                """
                query (${'$'}id: UUID!) {
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

    @Nested
    @DisplayName("query { users }")
    inner class UsersQueryTest {
        @Test
        fun `should return successful result containing empty list of users`() {
            // Given
            every { userService.getAllUsers(any()) } returns emptyList()

            // When
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

            // Then
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
            // Given
            val user = User(
                id = UUID.randomUUID(),
                username = "test-user",
            )

            every { userService.getAllUsers(any()) } returns listOf(user)

            // When
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

            // Then
            verify { userService.getAllUsers(input = UsersLookupInput(page = 0, size = 10)) }

            assertThat(result.errors)
                .isEmpty()
            assertThat(result.isDataPresent)
                .isTrue
            assertThat(result.getData<Map<Any, Any>>())
                .containsEntry(
                    QUERY.Users, listOf(
                        mapOf(
                            USER.Id to user.id.toString(),
                            USER.Username to user.username,
                        )
                    )
                )
        }
    }

    @Nested
    @DisplayName("mutation { createUser }")
    inner class CreateUserMutationTest {
        @Test
        @WithAnonymousUser
        fun `should create new user and return result object containing created user data`() {
            // Given
            val id = UUID.randomUUID()
            val username = "test-username"
            val password = "test-password"
            val expectedUser = User(id, username)

            every { userService.createUser(any()) } returns expectedUser

            // When
            val result = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                """
                mutation {
                    createUser(
                        input: {
                            username: "$username"
                            password: "$password"
                        }
                    ) {
                        id
                        username
                    }
                }
                """.trimIndent(),
                "$.data.createUser",
                User::class.java
            )

            // Then
            assertThat(result)
                .returns(expectedUser, from { it })

            verify(exactly = 1) { userService.createUser(CreateUserInput(username, password)) }
        }
    }

    @Nested
    @DisplayName("mutation { updateUser }")
    inner class UpdateUserMutationTest {
        @Test
        @WithMockUser
        fun `should update existing user and return result object containing updated user data`() {
            // Given
            val id = UUID.randomUUID()
            val username = "test-username"
            val password = "test-password"
            val expectedUser = User(id, username)

            every { userService.updateUser(any()) } returns expectedUser

            // When
            val result = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                """
                mutation {
                    updateUser(
                        input: {
                            id: "$id"
                            password: "$password"
                        }
                    ) {
                        id
                        username
                    }
                }
                """.trimIndent(),
                "$.data.updateUser",
                User::class.java
            )

            // Then
            assertThat(result)
                .returns(expectedUser, from { it })

            verify(exactly = 1) { userService.updateUser(UpdateUserInput(id, password)) }
        }
    }

    @Nested
    @DisplayName("mutation { deleteUser }")
    inner class DeleteUserMutationTest {
        @Test
        @WithMockUser
        fun `should delete existing user and return result object containing success field with value true`() {
            // Given
            val id = UUID.randomUUID()

            every { userService.deleteUser(any()) } returns true

            // When
            val result = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                """
                mutation {
                    deleteUser(
                        input: {
                            id: "$id"
                        }
                    )
                }
                """.trimIndent(),
                "$.data.deleteUser",
                Boolean::class.java
            )

            // Then
            assertThat(result)
                .isTrue()

            verify(exactly = 1) { userService.deleteUser(DeleteUserInput(id)) }
        }
    }
}
