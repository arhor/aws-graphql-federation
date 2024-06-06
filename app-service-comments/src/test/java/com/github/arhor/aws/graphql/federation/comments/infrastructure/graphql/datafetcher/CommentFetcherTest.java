package com.github.arhor.aws.graphql.federation.comments.infrastructure.graphql.datafetcher;

import com.github.arhor.aws.graphql.federation.comments.generated.graphql.DgsConstants.POST;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.DgsConstants.USER;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.Comment;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.CreateCommentInput;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.DeleteCommentInput;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.Post;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.UpdateCommentInput;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.User;
import com.github.arhor.aws.graphql.federation.comments.infrastructure.graphql.dataloader.PostCommentsBatchLoader;
import com.github.arhor.aws.graphql.federation.comments.infrastructure.graphql.dataloader.PostRepresentationBatchLoader;
import com.github.arhor.aws.graphql.federation.comments.infrastructure.graphql.dataloader.UserCommentsBatchLoader;
import com.github.arhor.aws.graphql.federation.comments.infrastructure.graphql.dataloader.UserRepresentationBatchLoader;
import com.github.arhor.aws.graphql.federation.comments.service.CommentService;
import com.github.arhor.aws.graphql.federation.comments.service.PostRepresentationService;
import com.github.arhor.aws.graphql.federation.comments.service.UserRepresentationService;
import com.github.arhor.aws.graphql.federation.starter.testing.ConstantsKt;
import com.github.arhor.aws.graphql.federation.starter.testing.GraphQLTestBase;
import com.github.arhor.aws.graphql.federation.starter.testing.WithMockCurrentUser;
import com.netflix.graphql.dgs.DgsQueryExecutor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static com.github.arhor.aws.graphql.federation.starter.testing.ConstantsKt.ZERO_UUID_STR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ContextConfiguration(
    classes = {
        CommentFetcher.class,
        PostRepresentationFetcher.class,
        UserRepresentationFetcher.class,
    }
)
class CommentFetcherTest extends GraphQLTestBase {

    private static final UUID USER_ID = ConstantsKt.getZERO_UUID_VAL();
    private static final UUID POST_ID = ConstantsKt.getOMNI_UUID_VAL();
    private static final UUID COMMENT_ID = ConstantsKt.getTEST_1_UUID_VAL();

    @MockBean
    private CommentService commentService;

    @MockBean
    private PostCommentsBatchLoader postCommentsBatchLoader;

    @MockBean
    private PostRepresentationBatchLoader postRepresentationBatchLoader;

    @MockBean
    private PostRepresentationService postRepresentationService;

    @MockBean
    private UserCommentsBatchLoader userCommentsBatchLoader;

    @MockBean
    private UserRepresentationBatchLoader userRepresentationBatchLoader;

    @MockBean
    private UserRepresentationService userRepresentationService;

    @Autowired
    private DgsQueryExecutor dgsQueryExecutor;

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(
            commentService,
            postCommentsBatchLoader,
            postRepresentationBatchLoader,
            postRepresentationService,
            userCommentsBatchLoader,
            userRepresentationBatchLoader,
            userRepresentationService
        );
    }

    @Nested
    @DisplayName("query { user { comments } }")
    class UserCommentsQueryTest {
        @Test
        void should_return_user_representation_with_a_list_of_expected_comments() {
            // Given
            final var expectedComments = List.of(
                Comment.newBuilder()
                    .id(COMMENT_ID)
                    .userId(USER_ID)
                    .postId(POST_ID)
                    .content("test-content")
                    .build()
            );
            final var userRepresentation =
                User.newBuilder()
                    .id(USER_ID)
                    .build();

            final var expectedUser =
                User.newBuilder()
                    .id(userRepresentation.getId())
                    .comments(expectedComments)
                    .build();

            given(userRepresentationBatchLoader.load(any()))
                .willReturn(CompletableFuture.completedFuture(Map.of(USER_ID, userRepresentation)));

            given(userCommentsBatchLoader.load(any()))
                .willReturn(CompletableFuture.completedFuture(Map.of(USER_ID, expectedComments)));

            // When
            final var result = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                // language=GraphQL
                """
                    query ($representations: [_Any!]!) {
                        _entities(representations: $representations) {
                            ... on User {
                                id
                                comments {
                                    id
                                    userId
                                    postId
                                    content
                                }
                                commentsDisabled
                            }
                        }
                    }""".stripIndent(),
                "$.data._entities[0]",
                Map.of("representations", List.of(Map.of("__typename", USER.TYPE_NAME, USER.Id, USER_ID))),
                User.class
            );

            // Then
            then(userRepresentationBatchLoader)
                .should()
                .load(Set.of(USER_ID));

            then(userCommentsBatchLoader)
                .should()
                .load(Set.of(USER_ID));

            assertThat(result)
                .isNotNull()
                .isEqualTo(expectedUser);
        }
    }

    @Nested
    @DisplayName("query { post { comments } }")
    class PostCommentsQueryTest {
        @Test
        void should_return_post_representation_with_a_list_of_expected_comments() {
            // Given
            final var expectedComments = List.of(
                Comment.newBuilder()
                    .id(COMMENT_ID)
                    .userId(USER_ID)
                    .postId(POST_ID)
                    .content("test-content")
                    .build()
            );
            final var postRepresentation =
                Post.newBuilder()
                    .id(POST_ID)
                    .build();

            final var expectedPost =
                Post.newBuilder()
                    .id(postRepresentation.getId())
                    .comments(expectedComments)
                    .build();

            given(postRepresentationBatchLoader.load(any()))
                .willReturn(CompletableFuture.completedFuture(Map.of(POST_ID, postRepresentation)));

            given(postCommentsBatchLoader.load(any()))
                .willReturn(CompletableFuture.completedFuture(Map.of(POST_ID, expectedComments)));

            // When
            final var result = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                // language=GraphQL
                """
                    query ($representations: [_Any!]!) {
                        _entities(representations: $representations) {
                            ... on Post {
                                id
                                comments {
                                    id
                                    userId
                                    postId
                                    content
                                }
                                commentsDisabled
                            }
                        }
                    }""".stripIndent(),
                "$.data._entities[0]",
                Map.of("representations", List.of(Map.of("__typename", POST.TYPE_NAME, POST.Id, POST_ID))),
                Post.class
            );

            // Then
            then(postRepresentationBatchLoader)
                .should()
                .load(Set.of(POST_ID));

            then(postCommentsBatchLoader)
                .should()
                .load(Set.of(POST_ID));

            assertThat(result)
                .isNotNull()
                .isEqualTo(expectedPost);
        }
    }

    @Nested
    @DisplayName("mutation { createComment }")
    @WithMockCurrentUser(id = ZERO_UUID_STR)
    class CreateCommentMutationTest {
        @Test
        void should_create_new_comment_and_return_result_object_containing_created_data() {
            // Given
            final var content = "test-password";
            final var expectedComment = new Comment(COMMENT_ID, USER_ID, POST_ID, null, content, null);

            given(commentService.createComment(any(), any()))
                .willReturn(expectedComment);

            // When
            final var result = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                // language=GraphQL
                """
                    mutation($userId: UUID!, $postId: UUID!, $content: String!) {
                        createComment(
                            input: {
                                userId: $userId
                                postId: $postId
                                content: $content
                            }
                        ) {
                            id
                            userId
                            postId
                            content
                        }
                    }""".stripIndent(),
                "$.data.createComment",
                Map.of("userId", USER_ID, "postId", POST_ID, "content", content),
                Comment.class
            );

            // Then
            then(commentService)
                .should()
                .createComment(eq(new CreateCommentInput(USER_ID, POST_ID, null, content)), any());

            assertThat(result)
                .isEqualTo(expectedComment);
        }
    }

    @Nested
    @DisplayName("mutation { updateComment }")
    @WithMockCurrentUser(id = ZERO_UUID_STR)
    class UpdateCommentMutationTest {
        @Test
        void should_update_existing_comment_and_return_result_object_containing_updated_data() {
            // Given
            final var content = "test-password";
            final var expectedComment = new Comment(COMMENT_ID, USER_ID, POST_ID, null, content, null);

            given(commentService.updateComment(any(), any()))
                .willReturn(expectedComment);

            // When
            final var result = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                // language=GraphQL
                """
                    mutation($id: UUID!, $content: String!) {
                        updateComment(
                            input: {
                                id: $id
                                content: $content
                            }
                        ) {
                            id
                            userId
                            postId
                            content
                        }
                    }""".stripIndent(),
                "$.data.updateComment",
                Map.of("id", COMMENT_ID, "content", content),
                Comment.class
            );

            // Then
            then(commentService)
                .should()
                .updateComment(eq(new UpdateCommentInput(COMMENT_ID, content)), any());

            assertThat(result)
                .isEqualTo(expectedComment);
        }
    }

    @Nested
    @DisplayName("mutation { deleteComment }")
    @WithMockCurrentUser(id = ZERO_UUID_STR)
    class DeleteCommentMutationTest {
        @ValueSource(booleans = { true, false })
        @ParameterizedTest
        void should_return_expected_result_calling_comment_service_with_expected_input(
            // Given
            final boolean expectedResult
        ) {
            final var expectedInput = new DeleteCommentInput(COMMENT_ID);

            given(commentService.deleteComment(any(), any()))
                .willReturn(expectedResult);

            // When
            final var result = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                // language=GraphQL
                """
                    mutation($id: UUID!) {
                        deleteComment(
                            input: {
                                id: $id
                            }
                        )
                    }""".stripIndent(),
                "$.data.deleteComment",
                Map.of("id", COMMENT_ID),
                Boolean.class
            );

            // Then
            then(commentService)
                .should()
                .deleteComment(eq(expectedInput), any());

            assertThat(result)
                .isNotNull()
                .isEqualTo(expectedResult);
        }
    }
}
