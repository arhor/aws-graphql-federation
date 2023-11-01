@file:Suppress("ClassName")

package com.github.arhor.aws.graphql.federation.comments.api.graphql.datafetcher

import com.github.arhor.aws.graphql.federation.comments.api.graphql.datafetcher.CommentFetcher
import com.github.arhor.aws.graphql.federation.comments.api.graphql.datafetcher.FederatedEntityFetchers
import com.github.arhor.aws.graphql.federation.comments.api.graphql.GlobalDataFetchingExceptionHandler
import com.github.arhor.aws.graphql.federation.comments.api.graphql.dataloader.CommentBatchLoader
import com.github.arhor.dgs.comments.generated.graphql.types.Comment
import com.github.arhor.dgs.comments.generated.graphql.types.CreateCommentInput
import com.github.arhor.dgs.comments.generated.graphql.types.CreateCommentResult
import com.github.arhor.dgs.comments.generated.graphql.types.UpdateCommentInput
import com.github.arhor.dgs.comments.generated.graphql.types.UpdateCommentResult
import com.github.arhor.aws.graphql.federation.comments.service.CommentService
import com.netflix.graphql.dgs.DgsQueryExecutor
import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration
import com.netflix.graphql.dgs.autoconfig.DgsExtendedScalarsAutoConfiguration
import com.ninjasquad.springmockk.MockkBean
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.from
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(
    classes = [
        CommentFetcher::class,
        DgsAutoConfiguration::class,
        DgsExtendedScalarsAutoConfiguration::class,
        FederatedEntityFetchers::class,
        GlobalDataFetchingExceptionHandler::class,
    ]
)
internal class CommentFetcherTest {

    @MockkBean
    private lateinit var commentService: CommentService

    @MockkBean
    private lateinit var commentsLoaderForUser: CommentBatchLoader.ForUser

    @MockkBean
    private lateinit var commentsLoaderForPost: CommentBatchLoader.ForPost

    @Autowired
    private lateinit var dgsQueryExecutor: DgsQueryExecutor

    @Nested
    inner class `mutation { createComment }` {
        @Test
        fun `should create new comment and return result object containing created data`() {
            // Given
            val id = -1L
            val userId = -2L
            val postId = -3L
            val content = "test-password"
            val expectedComment = Comment(id, userId, postId, content)

            every { commentService.createComment(any()) } returns CreateCommentResult(expectedComment)

            // When
            val result = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                """
                mutation {
                    createComment(
                        input: {
                            userId: "$userId"
                            postId: "$postId"
                            content: "$content"
                        }
                    ) {
                        comment {
                            id
                            userId
                            postId
                            content
                        }
                    }
                }
                """.trimIndent(),
                "$.data.createComment",
                CreateCommentResult::class.java
            )

            // Then
            assertThat(result)
                .returns(expectedComment, from { it.comment })

            verify(exactly = 1) { commentService.createComment(CreateCommentInput(userId, postId, content)) }
        }
    }

    @Nested
    inner class `mutation { updateComment }` {
        @Test
        fun `should update existing comment and return result object containing updated data`() {
            // Given
            val id = -1L
            val userId = -2L
            val postId = -3L
            val content = "test-password"
            val expectedComment = Comment(id, userId, postId, content)

            every { commentService.updateComment(any()) } returns UpdateCommentResult(expectedComment)

            // When
            val result = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                """
                mutation {
                    updateComment(
                        input: {
                            id: $id
                            content: "$content"
                        }
                    ) {
                        comment {
                            id
                            userId
                            postId
                            content
                        }
                    }
                }
                """.trimIndent(),
                "$.data.updateComment",
                UpdateCommentResult::class.java
            )

            // Then
            assertThat(result)
                .returns(expectedComment, from { it.comment })

            verify(exactly = 1) { commentService.updateComment(UpdateCommentInput(id, content)) }
        }
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }
}
