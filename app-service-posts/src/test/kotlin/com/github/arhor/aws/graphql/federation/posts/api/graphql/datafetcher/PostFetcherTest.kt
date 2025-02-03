package com.github.arhor.aws.graphql.federation.posts.api.graphql.datafetcher

import com.github.arhor.aws.graphql.federation.common.exception.EntityNotFoundException
import com.github.arhor.aws.graphql.federation.common.exception.Operation
import com.github.arhor.aws.graphql.federation.posts.api.graphql.dataloader.UserRepresentationBatchLoader
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.DgsConstants.POST
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.DgsConstants.POST_PAGE
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.DgsConstants.QUERY
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.Post
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.PostPage
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.PostsLookupInput
import com.github.arhor.aws.graphql.federation.posts.service.PostService
import com.github.arhor.aws.graphql.federation.posts.service.UserRepresentationService
import com.github.arhor.aws.graphql.federation.starter.testing.GraphQLTestBase
import com.github.arhor.aws.graphql.federation.starter.testing.TEST_1_UUID_VAL
import com.github.arhor.aws.graphql.federation.starter.testing.TEST_2_UUID_VAL
import com.netflix.graphql.dgs.DgsQueryExecutor
import com.ninjasquad.springmockk.MockkBean
import graphql.GraphQLError
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.from
import org.assertj.core.api.InstanceOfAssertFactories.LIST
import org.assertj.core.api.InstanceOfAssertFactories.MAP
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(
    classes = [
        PostFetcher::class,
        UserRepresentationFetcher::class,
    ]
)
internal class PostFetcherTest : GraphQLTestBase() {

    @MockkBean
    private lateinit var postService: PostService

    @MockkBean
    private lateinit var userRepresentationService: UserRepresentationService

    @MockkBean
    private lateinit var userRepresentationBatchLoader: UserRepresentationBatchLoader

    @Autowired
    private lateinit var dgsQueryExecutor: DgsQueryExecutor

    @AfterEach
    fun `confirm that all interactions with mocked dependencies were verified`() {
        confirmVerified(
            postService,
            userRepresentationService,
            userRepresentationBatchLoader,
        )
    }

    @Nested
    @DisplayName("query { post }")
    inner class PostQueryTest {
        @Test
        fun `should return expected post by id without any exceptions`() {
            // Given
            val expectedErrors = emptyList<GraphQLError>()
            val expectedPresent = true
            val expectedData =
                mapOf(
                    QUERY.Post to mapOf(
                        POST.Id to POST_ID.toString(),
                        POST.UserId to USER_ID.toString(),
                        POST.Title to "test-title",
                        POST.Content to "test-content",
                    )
                )

            every { postService.getPostById(any()) } answers {
                Post(
                    id = firstArg(),
                    userId = USER_ID,
                    title = "test-title",
                    content = "test-content",
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
                    }
                }
                """.trimIndent(),
                mapOf(POST.Id to POST_ID)
            )

            // Then
            verify(exactly = 1) { postService.getPostById(POST_ID) }

            assertThat(result)
                .returns(expectedErrors, from { it.errors })
                .returns(expectedPresent, from { it.isDataPresent })
                .returns(expectedData, from { it.getData<Any>() })
        }

        @Test
        fun `should return GQL error trying to find post by incorrect id`() {
            // Given
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
                    }
                }
                """.trimIndent(),
                mapOf(POST.Id to POST_ID)
            )

            // Then
            verify(exactly = 1) { postService.getPostById(POST_ID) }

            assertThat(result.errors)
                .singleElement()
                .returns(listOf(QUERY.Post), from { it.path })
        }
    }

    @Nested
    @DisplayName("query { posts }")
    inner class PostsQueryTest {
        @Test
        fun `should return expected posts page when processing query without explicit input`() {
            // Given
            val post = Post(
                id = POST_ID,
                userId = USER_ID,
                title = "test-title",
                content = "test-content",
            )
            val page = PostPage(
                data = listOf(post),
                page = 1,
                size = 20,
                hasPrev = false,
                hasNext = false,
            )

            every { postService.getPostPage(any()) } returns page

            // When
            val result = dgsQueryExecutor.execute(
                """
                query {
                    posts {
                        data {
                            id
                            userId
                            title
                            content                        
                        }
                        page
                        size
                        hasPrev
                        hasNext
                    }
                }
                """.trimIndent()
            )

            // Then
            verify(exactly = 1) { postService.getPostPage(PostsLookupInput()) }

            assertThat(result)
                .returns(emptyList(), from { it.errors })
                .returns(true, from { it.isDataPresent })
                .extracting({ it.getData<Any>() }, MAP)
                .extractingByKey(QUERY.Posts, MAP)
                .satisfies(
                    { assertThat(it[POST_PAGE.Page]).isEqualTo(page.page) },
                    { assertThat(it[POST_PAGE.Size]).isEqualTo(page.size) },
                    { assertThat(it[POST_PAGE.HasPrev]).isEqualTo(page.hasPrev) },
                    { assertThat(it[POST_PAGE.HasNext]).isEqualTo(page.hasNext) },
                )
                .extractingByKey(POST_PAGE.Data, LIST)
                .singleElement(MAP)
                .satisfies(
                    { assertThat(it[POST.Id]).isEqualTo("${post.id}") },
                    { assertThat(it[POST.UserId]).isEqualTo("${post.userId}") },
                    { assertThat(it[POST.Title]).isEqualTo(post.title) },
                    { assertThat(it[POST.Content]).isEqualTo(post.content) },
                )
        }

        @Test
        fun `should return expected posts page when processing query with explicit input`() {
            // Given
            val post = Post(
                id = POST_ID,
                userId = USER_ID,
                title = "test-title",
                content = "test-content",
            )
            val page = PostPage(
                data = listOf(post),
                page = 1,
                size = 20,
                hasPrev = false,
                hasNext = false,
            )

            every { postService.getPostPage(any()) } returns page

            // When
            val result = dgsQueryExecutor.execute(
                """
                query(${'$'}input: PostsLookupInput!) {
                    posts(input: ${'$'}input) {
                        data {
                            id
                            userId
                            title
                            content                        
                        }
                        page
                        size
                        hasPrev
                        hasNext
                    }
                },
                """.trimIndent(),
                mapOf("input" to PostsLookupInput())
            )

            // Then
            verify(exactly = 1) { postService.getPostPage(PostsLookupInput()) }

            assertThat(result)
                .returns(emptyList(), from { it.errors })
                .returns(true, from { it.isDataPresent })
                .extracting({ it.getData<Any>() }, MAP)
                .extractingByKey(QUERY.Posts, MAP)
                .satisfies(
                    { assertThat(it[POST_PAGE.Page]).isEqualTo(page.page) },
                    { assertThat(it[POST_PAGE.Size]).isEqualTo(page.size) },
                    { assertThat(it[POST_PAGE.HasPrev]).isEqualTo(page.hasPrev) },
                    { assertThat(it[POST_PAGE.HasNext]).isEqualTo(page.hasNext) },
                )
                .extractingByKey(POST_PAGE.Data, LIST)
                .singleElement(MAP)
                .satisfies(
                    { assertThat(it[POST.Id]).isEqualTo("${post.id}") },
                    { assertThat(it[POST.UserId]).isEqualTo("${post.userId}") },
                    { assertThat(it[POST.Title]).isEqualTo(post.title) },
                    { assertThat(it[POST.Content]).isEqualTo(post.content) },
                )
        }
    }

    @Nested
    @DisplayName("mutation { createPost }")
    inner class CreatePostMutationTest {

    }

    @DisplayName("mutation { updatePost }")
    inner class UpdatePostMutationTest {}

    @DisplayName("mutation { deletePost }")
    inner class DeletePostMutationTest {}

    companion object {
        private val USER_ID = TEST_1_UUID_VAL
        private val POST_ID = TEST_2_UUID_VAL
    }
}
