package com.github.arhor.aws.graphql.federation.posts.api.graphql.datafetcher

import com.github.arhor.aws.graphql.federation.common.exception.EntityNotFoundException
import com.github.arhor.aws.graphql.federation.common.exception.Operation
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.DgsConstants.POST
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.DgsConstants.QUERY
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.DgsConstants.USER
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.Post
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.User
import com.github.arhor.aws.graphql.federation.posts.api.graphql.dataloader.TagBatchLoader
import com.github.arhor.aws.graphql.federation.posts.api.graphql.dataloader.UserRepresentationBatchLoader
import com.github.arhor.aws.graphql.federation.posts.service.PostService
import com.github.arhor.aws.graphql.federation.posts.service.UserRepresentationService
import com.github.arhor.aws.graphql.federation.starter.testing.GraphQLTestBase
import com.github.arhor.aws.graphql.federation.starter.testing.OMNI_UUID_VAL
import com.github.arhor.aws.graphql.federation.starter.testing.ZERO_UUID_VAL
import com.netflix.graphql.dgs.DgsQueryExecutor
import com.ninjasquad.springmockk.MockkBean
import graphql.GraphQLError
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.from
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import java.util.concurrent.CompletableFuture

@ContextConfiguration(
    classes = [
        TagFetcher::class,
        PostFetcher::class,
        UserRepresentationFetcher::class,
    ]
)
class PostFetcherTest : GraphQLTestBase() {

    @MockkBean
    private lateinit var postService: PostService

    @MockkBean
    private lateinit var tagBatchLoader: TagBatchLoader

    @MockkBean
    private lateinit var userRepresentationService: UserRepresentationService

    @MockkBean
    private lateinit var userRepresentationBatchLoader: UserRepresentationBatchLoader

    @Autowired
    private lateinit var dgsQueryExecutor: DgsQueryExecutor

    @AfterEach
    fun tearDown() {
        confirmVerified(
            postService,
            tagBatchLoader,
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
                """,
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
                """,
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
    @DisplayName("query { user { posts } }")
    inner class UserPostsQueryTest {
        @Test
        fun `should return user representation with a list of expected posts`() {
            // Given
            val expectedUser = User(id = USER_ID)

            every { userRepresentationBatchLoader.load(any()) } returns CompletableFuture.completedFuture(mapOf(USER_ID to expectedUser))

            // When
            val result = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                """
                query (${'$'}representations: [_Any!]!) {
                    _entities(representations: ${'$'}representations) {
                        ... on User {
                            id
                            postsDisabled
                        }
                    }
                }""".trimIndent(),
                "$.data._entities[0]",
                mapOf(
                    "representations" to listOf(
                        mapOf(
                            "__typename" to USER.TYPE_NAME,
                            USER.Id to USER_ID
                        )
                    )
                ),
                User::class.java
            )

            // Then
            verify(exactly = 1) { userRepresentationBatchLoader.load(setOf(USER_ID)) }

            assertThat(result)
                .isNotNull()
                .isEqualTo(expectedUser)
        }
    }

    companion object {
        private val USER_ID = ZERO_UUID_VAL
        private val POST_ID = OMNI_UUID_VAL
    }
}
