package com.github.arhor.aws.graphql.federation.posts.api.graphql.datafetcher

import com.github.arhor.aws.graphql.federation.posts.api.graphql.dataloader.UserRepresentationBatchLoader
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.DgsConstants.MUTATION
import com.github.arhor.aws.graphql.federation.posts.service.PostService
import com.github.arhor.aws.graphql.federation.posts.service.UserRepresentationService
import com.github.arhor.aws.graphql.federation.starter.testing.GraphQLTestBase
import com.github.arhor.aws.graphql.federation.starter.testing.TEST_1_UUID_VAL
import com.github.arhor.aws.graphql.federation.starter.testing.WithMockCurrentUser
import com.netflix.graphql.dgs.DgsQueryExecutor
import com.ninjasquad.springmockk.MockkBean
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(
    classes = [
        PostFetcher::class,
        UserRepresentationFetcher::class,
    ]
)
internal class UserRepresentationFetcherTest : GraphQLTestBase() {

    @MockkBean
    private lateinit var postService: PostService

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
            userRepresentationService,
            userRepresentationBatchLoader,
        )
    }

    @Nested
    @DisplayName("mutation { toggleUserPosts    }")
    inner class UserPostsQueryTest {
        @ValueSource(booleans = [true, false])
        @ParameterizedTest
        @WithMockCurrentUser(roles = ["ADMIN"])
        fun `should return user representation with a list of expected posts`(
            // Given
            mutationResult: Boolean,
        ) {
            val expectedData = mapOf(MUTATION.ToggleUserPosts to mutationResult)

            every { userRepresentationService.toggleUserPosts(any()) } returns mutationResult

            // When
            val result = dgsQueryExecutor.execute(
                """
                mutation (${'$'}userId: UUID!) {
                    toggleUserPosts(userId: ${'$'}userId)
                }
                """.trimIndent(),
                mapOf("userId" to USER_ID),
            )

            // Then
            verify(exactly = 1) { userRepresentationService.toggleUserPosts(USER_ID) }

            assertThat(result)
                .satisfies(
                    { assertThat(it.errors).isEmpty() },
                    { assertThat(it.isDataPresent).isTrue() },
                    { assertThat(it.getData<Any>()).isEqualTo(expectedData) },
                )
        }
    }

    companion object {
        private val USER_ID = TEST_1_UUID_VAL
    }
}
