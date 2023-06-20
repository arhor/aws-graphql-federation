package com.github.arhor.dgs.users.graphql.fetcher

import com.github.arhor.dgs.users.generated.graphql.types.Setting
import com.github.arhor.dgs.users.generated.graphql.types.User
import com.github.arhor.dgs.users.service.UserService
import com.netflix.graphql.dgs.DgsQueryExecutor
import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration
import com.netflix.graphql.dgs.autoconfig.DgsExtendedScalarsAutoConfiguration
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(
    classes = [
        DgsAutoConfiguration::class,
        DgsExtendedScalarsAutoConfiguration::class,
        UserFetcher::class,
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
}
