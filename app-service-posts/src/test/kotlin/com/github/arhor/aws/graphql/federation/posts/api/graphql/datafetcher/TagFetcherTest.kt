package com.github.arhor.aws.graphql.federation.posts.api.graphql.datafetcher

import com.github.arhor.aws.graphql.federation.posts.api.graphql.dataloader.TagBatchLoader
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.DgsConstants.POST
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.DgsConstants.QUERY
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.Post
import com.github.arhor.aws.graphql.federation.posts.service.PostService
import com.github.arhor.aws.graphql.federation.starter.testing.GraphQLTestBase
import com.github.arhor.aws.graphql.federation.starter.testing.OMNI_UUID_VAL
import com.netflix.graphql.dgs.DgsQueryExecutor
import com.ninjasquad.springmockk.MockkBean
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import java.util.concurrent.CompletableFuture.completedFuture

@ContextConfiguration(
    classes = [
        TagFetcher::class,
        PostFetcher::class,
    ]
)
internal class TagFetcherTest : GraphQLTestBase() {

    @MockkBean
    private lateinit var postService: PostService

    @MockkBean
    private lateinit var tagBatchLoader: TagBatchLoader

    @Autowired
    private lateinit var dgsQueryExecutor: DgsQueryExecutor

    @AfterEach
    fun tearDown() {
        confirmVerified(
            postService,
            tagBatchLoader,
        )
    }

    @Nested
    @DisplayName("query { post { tags } }")
    inner class PostQueryTest {
        @Test
        fun `should return expected post by id without any exceptions`() {
            // Given
            val post = mockk<Post>()
            val expectedData = mapOf(
                QUERY.Post to mapOf(
                    POST.Id to POST_ID.toString(),
                    POST.Tags to emptyList<String>(),
                )
            )

            every { post.id } returns POST_ID
            every { postService.getPostById(any()) } returns post
            every { tagBatchLoader.load(any()) } returns completedFuture(mapOf(POST_ID to emptyList()))

            // When
            val result = dgsQueryExecutor.execute(
                """
                query (${'$'}id: UUID!) {
                    post(id: ${'$'}id) {
                        id
                        tags
                    }
                }
                """.trimIndent(),
                mapOf(POST.Id to POST_ID),
            )

            // Then
            verify(exactly = 1) {
                postService.getPostById(POST_ID)
                tagBatchLoader.load(setOf(POST_ID))
            }

            assertThat(result)
                .satisfies(
                    { assertThat(it.errors).isEmpty() },
                    { assertThat(it.isDataPresent).isTrue() },
                    { assertThat(it.getData<Any>()).isEqualTo(expectedData) },
                )
        }
    }

    companion object {
        private val POST_ID = OMNI_UUID_VAL
    }
}
