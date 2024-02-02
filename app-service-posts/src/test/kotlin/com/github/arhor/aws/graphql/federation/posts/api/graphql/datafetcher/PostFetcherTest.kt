@file:Suppress("ClassName")

package com.github.arhor.aws.graphql.federation.posts.api.graphql.datafetcher

import com.github.arhor.aws.graphql.federation.common.exception.EntityNotFoundException
import com.github.arhor.aws.graphql.federation.common.exception.Operation
import com.github.arhor.aws.graphql.federation.dgs.GlobalDataFetchingExceptionHandler
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.DgsConstants.POST
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.DgsConstants.QUERY
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.Option
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.Post
import com.github.arhor.aws.graphql.federation.posts.service.PostService
import com.netflix.graphql.dgs.DgsQueryExecutor
import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration
import com.netflix.graphql.dgs.autoconfig.DgsExtendedScalarsAutoConfiguration
import com.ninjasquad.springmockk.MockkBean
import graphql.GraphQLError
import io.mockk.every
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.from
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(
    classes = [
        DgsAutoConfiguration::class,
        DgsExtendedScalarsAutoConfiguration::class,
        GlobalDataFetchingExceptionHandler::class,
        PostFetcher::class,
    ]
)
internal class PostFetcherTest {

    @MockkBean
    private lateinit var postService: PostService

    @Autowired
    private lateinit var dgsQueryExecutor: DgsQueryExecutor

    @Nested
    inner class `query { post }` {
        @Test
        fun `should return expected post by id without any exceptions`() {
            // given
            val expectedId = 1L

            val expectedErrors = emptyList<GraphQLError>()
            val expectedPresent = true
            val expectedData =
                mapOf(
                    QUERY.Post to mapOf(
                        POST.Id to expectedId,
                        POST.UserId to 1L,
                        POST.Header to "test-header",
                        POST.Content to "test-content",
                        POST.Options to listOf(Option.NSFW.name),
                    )
                )

            every { postService.getPostById(any()) } answers {
                Post(
                    id = firstArg(),
                    userId = 1L,
                    header = "test-header",
                    content = "test-content",
                    options = listOf(Option.NSFW),
                )
            }

            // When
            val result = dgsQueryExecutor.execute(
                """
                query (${'$'}id: Long!) {
                    post(id: ${'$'}id) {
                        id
                        userId
                        header
                        content
                        options
                    }
                }
                """,
                mapOf(POST.Id to expectedId)
            )

            // then
            assertThat(result)
                .returns(expectedErrors, from { it.errors })
                .returns(expectedPresent, from { it.isDataPresent })
                .returns(expectedData, from { it.getData<Any>() })
        }

        @Test
        fun `should return GQL error trying to find post by incorrect id`() {
            // given
            val id = 1L

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
                query (${'$'}id: Long!) {
                    post(id: ${'$'}id) {
                        id
                        userId
                        header
                        content
                        options
                    }
                }
                """,
                mapOf(POST.Id to id)
            )

            // then
            assertThat(result.errors)
                .singleElement()
                .returns(listOf(QUERY.Post), from { it.path })
        }
    }
}
