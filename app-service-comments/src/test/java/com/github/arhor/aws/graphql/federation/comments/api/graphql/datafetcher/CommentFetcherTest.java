package com.github.arhor.aws.graphql.federation.comments.api.graphql.datafetcher;

import com.github.arhor.aws.graphql.federation.comments.api.graphql.dataloader.PostCommentsBatchLoader;
import com.github.arhor.aws.graphql.federation.comments.api.graphql.dataloader.UserCommentsBatchLoader;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.Comment;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.CreateCommentInput;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.CreateCommentResult;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.UpdateCommentInput;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.UpdateCommentResult;
import com.github.arhor.aws.graphql.federation.comments.service.CommentService;
import com.netflix.graphql.dgs.DgsQueryExecutor;
import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration;
import com.netflix.graphql.dgs.autoconfig.DgsExtendedScalarsAutoConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.from;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@MockBean(
    classes = {
        CommentService.class,
        UserCommentsBatchLoader.class,
        PostCommentsBatchLoader.class,
    }
)
@SpringBootTest(
    classes = {
        CommentFetcher.class,
        DgsAutoConfiguration.class,
        DgsExtendedScalarsAutoConfiguration.class,
    }
)
class CommentFetcherTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private DgsQueryExecutor dgsQueryExecutor;

    @Nested
    @DisplayName("mutation { createComment }")
    class CreateCommentMutationTest {
        @Test
        void should_create_new_comment_and_return_result_object_containing_created_data() {
            // Given
            var id = UUID.randomUUID();
            var userId = UUID.randomUUID();
            var postId = UUID.randomUUID();
            var content = "test-password";
            var expectedComment = new Comment(id, userId, postId, content);

            given(commentService.createComment(any()))
                .willReturn(new CreateCommentResult(expectedComment));

            // When
            var result = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                """
                    mutation($userId: UUID!, $postId: UUID!, $content: String!) {
                        createComment(
                            input: {
                                userId: $userId
                                postId: $postId
                                content: $content
                            }
                        ) {
                            comment {
                                id
                                userId
                                postId
                                content
                            }
                        }
                    }""".stripIndent(),
                "$.data.createComment",
                Map.of("userId", userId, "postId", postId, "content", content),
                CreateCommentResult.class
            );

            // Then
            then(commentService)
                .should()
                .createComment(new CreateCommentInput(userId, postId, content));

            assertThat(result)
                .returns(expectedComment, from(CreateCommentResult::getComment));
        }
    }

    @Nested
    @DisplayName("mutation { updateComment }")
    class UpdateCommentMutationTest {
        @Test
        void should_update_existing_comment_and_return_result_object_containing_updated_data() {
            // Given
            var id = UUID.randomUUID();
            var userId = UUID.randomUUID();
            var postId = UUID.randomUUID();
            var content = "test-password";
            var expectedComment = new Comment(id, userId, postId, content);

            given(commentService.updateComment(any()))
                .willReturn(new UpdateCommentResult(expectedComment));

            // When
            var result = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                """
                    mutation($id: UUID!, $content: String!) {
                        updateComment(
                            input: {
                                id: $id
                                content: $content
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
                    """.stripIndent(),
                "$.data.updateComment",
                Map.of("id", id, "content", content),
                UpdateCommentResult.class
            );

            // Then
            then(commentService)
                .should()
                .updateComment(new UpdateCommentInput(id, content));

            assertThat(result)
                .returns(expectedComment, from(UpdateCommentResult::getComment));
        }
    }
}
