package com.github.arhor.aws.graphql.federation.users.api.graphql.datafetcher

import com.github.arhor.aws.graphql.federation.common.exception.EntityNotFoundException
import com.github.arhor.aws.graphql.federation.common.exception.Operation
import com.github.arhor.aws.graphql.federation.starter.testing.GraphQLTestBase
import com.github.arhor.aws.graphql.federation.starter.testing.OMNI_UUID_VAL
import com.github.arhor.aws.graphql.federation.starter.testing.WithMockCurrentUser
import com.github.arhor.aws.graphql.federation.starter.testing.ZERO_UUID_STR
import com.github.arhor.aws.graphql.federation.starter.testing.ZERO_UUID_VAL
import com.github.arhor.aws.graphql.federation.users.generated.graphql.DgsConstants.MUTATION
import com.github.arhor.aws.graphql.federation.users.generated.graphql.DgsConstants.QUERY
import com.github.arhor.aws.graphql.federation.users.generated.graphql.DgsConstants.UPDATE_USER_INPUT
import com.github.arhor.aws.graphql.federation.users.generated.graphql.DgsConstants.USER
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.CreateUserInput
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.DeleteUserInput
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.UpdateUserInput
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.User
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.UserPage
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.UsersLookupInput
import com.github.arhor.aws.graphql.federation.users.service.UserService
import com.netflix.graphql.dgs.DgsQueryExecutor
import com.ninjasquad.springmockk.MockkBean
import graphql.GraphQLError
import io.mockk.every
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.from
import org.assertj.core.api.InstanceOfAssertFactories.MAP
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import java.util.UUID

@ContextConfiguration(
    classes = [
        UserFetcher::class,
    ]
)
class UserFetcherTest : GraphQLTestBase() {

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
            val expectedId = USER_ID
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
                mapOf(USER.Id to USER_ID)
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
            every { userService.getUserPage(any()) } returns UserPage(data = emptyList())

            // When
            val result = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                """
                query {
                    users {
                        data {
                            id
                            username                    
                        }
                        page
                        size
                        hasPrev
                        hasNext
                    }
                }
                """.trimIndent(),
                "$.data.users",
                UserPage::class.java
            )

            // Then
            verify { userService.getUserPage(input = UsersLookupInput(page = 0, size = 20)) }

            assertThat(result.data)
                .isEmpty()
        }

        @Test
        fun `should return successful result containing list with single expected user`() {
            // Given
            val user = User(
                id = USER_ID,
                username = "test-user",
            )

            every { userService.getUserPage(any()) } returns UserPage(data = listOf(user))

            // When
            val result = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                """
                query {
                    users(input: { page: 0, size: 10 }) {
                        data {
                            id
                            username
                        }
                    }
                }
                """.trimIndent(),
                "$.data.users",
                UserPage::class.java
            )

            // Then
            verify { userService.getUserPage(input = UsersLookupInput(page = 0, size = 10)) }

            assertThat(result.data)
                .singleElement()
                .isEqualTo(user)
        }
    }

    @Nested
    @DisplayName("mutation { createUser }")
    inner class CreateUserMutationTest {
        @Test
        fun `should create new user and return result object containing created user data`() {
            // Given
            val username = "username"
            val password = "Password123"
            val expectedUser = User(USER_ID, username)

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
    @WithMockCurrentUser(id = ZERO_UUID_STR)
    inner class UpdateUserMutationTest {
        @Test
        fun `should update existing user and return result containing user data without errors`() {
            // Given
            val id = ZERO_UUID_VAL
            val username = "username"
            val password = "Password123"
            val expectedUser = User(id, username)

            every { userService.updateUser(any()) } returns expectedUser

            // When
            val result = executeUpdateUserMutation(id, password)

            // Then
            verify(exactly = 1) { userService.updateUser(UpdateUserInput(id, password)) }

            assertThat(result.errors)
                .isEmpty()

            assertThat(result.getData<Map<String, Any>>())
                .isNotNull()
                .hasEntrySatisfying(MUTATION.UpdateUser) {
                    assertThat(it)
                        .asInstanceOf(MAP)
                        .containsEntry(USER.Id, expectedUser.id.toString())
                        .containsEntry(USER.Username, expectedUser.username)
                }
        }

        @Test
        fun `should return an error trying to update a user different from authenticated one`() {
            // Given
            val id = OMNI_UUID_VAL
            val password = "test-password"

            // When
            val result = executeUpdateUserMutation(id, password)

            // Then
            assertThat(result.errors)
                .isNotEmpty()
        }

        private fun executeUpdateUserMutation(id: UUID, password: String) = dgsQueryExecutor.execute(
            """
            mutation UpdateUserTest(${'$'}id: UUID!, ${'$'}password: String!) {
                updateUser(
                    input: {
                        id: ${'$'}id
                        password: ${'$'}password
                    }
                ) {
                    id
                    username
                }
            }
            """.trimIndent(),
            mapOf(UPDATE_USER_INPUT.Id to id, UPDATE_USER_INPUT.Password to password)
        )
    }

    @Nested
    @DisplayName("mutation { deleteUser }")
    @WithMockCurrentUser(id = ZERO_UUID_STR)
    inner class DeleteUserMutationTest {
        @Test
        fun `should delete existing user and return result object containing success field with value true`() {
            // Given
            val id = ZERO_UUID_VAL

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

    companion object {
        private val USER_ID = ZERO_UUID_VAL
    }
}
