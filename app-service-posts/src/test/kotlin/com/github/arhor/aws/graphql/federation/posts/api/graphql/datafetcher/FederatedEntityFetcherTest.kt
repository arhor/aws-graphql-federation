package com.github.arhor.aws.graphql.federation.posts.api.graphql.datafetcher

import com.github.arhor.aws.graphql.federation.posts.generated.graphql.DgsConstants.USER
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.User
import com.github.arhor.aws.graphql.federation.posts.service.UserRepresentationService
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
import java.util.UUID

@SpringBootTest(
    classes = [
        DgsAutoConfiguration::class,
        DgsExtendedScalarsAutoConfiguration::class,
        FederatedEntityFetcher::class,
    ]
)
class FederatedEntityFetcherTest {

    @MockkBean
    private lateinit var userRepresentationService: UserRepresentationService

    @Autowired
    private lateinit var dgsQueryExecutor: DgsQueryExecutor

    @Test
    fun `should create new user representation for the given id`() {
        // Given
        val userId = UUID.randomUUID()
        val expectedUser = User(id = userId)

        every { userRepresentationService.findUserRepresentation(any()) } returns expectedUser

        // When
        val result = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
            """
                query (${'$'}representations: [_Any!]!) {
                    _entities(representations: ${'$'}representations) {
                        ... on User {
                            id
                        }
                    }
                }""".trimIndent(),
            "$.data._entities[0]",
            mapOf("representations" to listOf(mapOf("__typename" to USER.TYPE_NAME, USER.Id to userId))),
            User::class.java
        )

        // Then
        verify(exactly = 1) { userRepresentationService.findUserRepresentation(userId) }

        assertThat(result)
            .isNotNull()
            .isEqualTo(expectedUser)
    }
}
