package com.github.arhor.aws.graphql.federation.posts.api.graphql.datafetcher

import com.github.arhor.aws.graphql.federation.common.exception.EntityNotFoundException
import com.github.arhor.aws.graphql.federation.common.exception.Operation
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.DgsConstants.POST
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.DgsConstants.QUERY
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.Option
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.Post
import com.github.arhor.aws.graphql.federation.posts.service.PostService
import com.github.arhor.aws.graphql.federation.spring.dgs.GlobalDataFetchingExceptionHandler
import com.netflix.graphql.dgs.DgsQueryExecutor
import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration
import com.netflix.graphql.dgs.autoconfig.DgsExtendedScalarsAutoConfiguration
import com.ninjasquad.springmockk.MockkBean
import graphql.GraphQLError
import io.mockk.every
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.from
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.UUID

@SpringBootTest(
    classes = [
        DgsAutoConfiguration::class,
        DgsExtendedScalarsAutoConfiguration::class,
        GlobalDataFetchingExceptionHandler::class,
        PostFetcher::class,
    ]
)
class PostFetcherTest {

    @MockkBean
    private lateinit var postService: PostService

    @Autowired
    private lateinit var dgsQueryExecutor: DgsQueryExecutor

    @Nested
    @DisplayName("query { post }")
    inner class PostQueryTest {
        @Test
        fun `should return expected post by id without any exceptions`() {
            // Given
            val expectedId = UUID.randomUUID()
            val expectedUserId = UUID.randomUUID()

            val expectedErrors = emptyList<GraphQLError>()
            val expectedPresent = true
            val expectedData =
                mapOf(
                    QUERY.Post to mapOf(
                        POST.Id to expectedId.toString(),
                        POST.UserId to expectedUserId.toString(),
                        POST.Title to "test-title",
                        POST.Content to "test-content",
                        POST.Options to listOf(Option.NSFW.name),
                    )
                )

            every { postService.getPostById(any()) } answers {
                Post(
                    id = firstArg(),
                    userId = expectedUserId,
                    title = "test-title",
                    content = "test-content",
                    options = listOf(Option.NSFW),
                )
            }

            // When
            val result = dgsQueryExecutor.execute(
                """
                query (${'$'}id: UUID!) {
                    post(id: ${'$'}id) {
                        id
                        userId
                        title
                        content
                        options
                    }
                }
                """,
                mapOf(POST.Id to expectedId)
            )

            // Then
            assertThat(result)
                .returns(expectedErrors, from { it.errors })
                .returns(expectedPresent, from { it.isDataPresent })
                .returns(expectedData, from { it.getData<Any>() })
        }

        @Test
        fun `should return GQL error trying to find post by incorrect id`() {
            // Given
            val id = UUID.randomUUID()

            every { postService.getPostById(any()) } answers {
                throw EntityNotFoundException(
                    entity = POST.TYPE_NAME,
                    condition = "${POST.Id} = ${firstArg<Long>()}",
                    operation = Operation.LOOKUP,
                )
            }

            // When
            val result = dgsQueryExecutor.execute(
                """
                query (${'$'}id: UUID!) {
                    post(id: ${'$'}id) {
                        id
                        userId
                        title
                        content
                        options
                    }
                }
                """,
                mapOf(POST.Id to id)
            )

            // Then
            assertThat(result.errors)
                .singleElement()
                .returns(listOf(QUERY.Post), from { it.path })
        }
    }
}
