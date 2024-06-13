package com.github.arhor.aws.graphql.federation.comments.data.repository;

import com.github.arhor.aws.graphql.federation.comments.data.entity.CommentEntity;
import com.github.arhor.aws.graphql.federation.comments.data.entity.callback.CommentEntityCallback;
import com.github.arhor.aws.graphql.federation.comments.data.repository.mapping.CommentsNumberByPostIdResultSetExtractor;
import com.github.arhor.aws.graphql.federation.starter.testing.ConstantsKt;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@ContextConfiguration(
    classes = {
        CommentEntityCallback.class,
        CommentsNumberByPostIdResultSetExtractor.class,
    }
)
public class CommentRepositoryTest extends RepositoryTestBase {

    @Nested
    @DisplayName("CommentRepository :: findAllByUserIdIn")
    class FindAllByUserIdInTest {
        @Test
        void should_return_expected_list_of_comments_by_user_ids() {
            // Given
            final var user1 = createUser(ConstantsKt.getTEST_1_UUID_VAL());
            final var user2 = createUser(ConstantsKt.getTEST_2_UUID_VAL());
            final var user3 = createUser(ConstantsKt.getTEST_3_UUID_VAL());

            final var post1 = createPost(ConstantsKt.getOMNI_UUID_VAL());

            final var user1Comments = List.of(
                createComment(user1, post1),
                createComment(user1, post1)
            );
            final var user2Comments = List.of(
                createComment(user2, post1),
                createComment(user2, post1)
            );
            final var user3Comments = List.of(
                createComment(user3, post1),
                createComment(user3, post1)
            );

            final var expectedComments = Stream.concat(user1Comments.stream(), user2Comments.stream()).toList();

            // When
            final var result = commentRepository.findAllByUserIdIn(
                expectedComments
                    .stream()
                    .map(CommentEntity::userId)
                    .toList(),
                Sort.unsorted()
            );

            // Then
            assertThat(result)
                .isNotNull()
                .containsExactlyElementsOf(expectedComments)
                .doesNotContainAnyElementsOf(user3Comments);
        }

        @Test
        void should_return_empty_stream_when_post_ids_is_empty() {
            // Given
            final List<UUID> userIds = List.of();

            // When
            final var result = commentRepository.findAllByUserIdIn(userIds, Sort.unsorted());

            // Then
            assertThat(result)
                .isEmpty();
        }
    }

    @Nested
    @DisplayName("CommentRepository :: findAllByPrntIdNullAndPostIdIn")
    class FindAllByPrntIdNullAndPostIdInTest {
        @Test
        void should_return_expected_list_of_top_level_comments_by_post_ids() {
            // Given
            final var user1 = createUser(ConstantsKt.getZERO_UUID_VAL());
            final var user2 = createUser(ConstantsKt.getOMNI_UUID_VAL());

            final var post1 = createPost(ConstantsKt.getTEST_1_UUID_VAL());
            final var post2 = createPost(ConstantsKt.getTEST_2_UUID_VAL());
            final var post3 = createPost(ConstantsKt.getTEST_3_UUID_VAL());

            final var post1Comment1 = createComment(user1, post1);
            final var post1Comment2 = createComment(user2, post1);

            final var post2Comment1 = createComment(user1, post2);
            final var post2Comment2 = createComment(user2, post2, post2Comment1);

            final var post3Comment1 = createComment(user1, post3);
            final var post3Comment2 = createComment(user2, post3, post3Comment1);

            // When
            final var result =
                commentRepository.findAllByPrntIdNullAndPostIdIn(
                    List.of(
                        post1.id(),
                        post2.id()
                    ),
                    Sort.unsorted()
                );

            // Then
            assertThat(result)
                .isNotNull()
                .containsExactly(post1Comment1, post1Comment2, post2Comment1)
                .doesNotContain(post2Comment2, post3Comment1, post3Comment2);
        }

        @Test
        void should_return_empty_stream_when_post_ids_is_empty() {
            // Given
            final List<UUID> postIds = List.of();

            // When
            final var result = commentRepository.findAllByPrntIdNullAndPostIdIn(postIds, Sort.unsorted());

            // Then
            assertThat(result)
                .isEmpty();
        }
    }

    @Nested
    @DisplayName("CommentRepository :: countCommentsByPostIds")
    class CountCommentsByPostIdsTest {
        @Test
        void should_return_expected_number_of_comments_for_a_given_post_ids() {
            // Given
            final var user = createUser(ConstantsKt.getZERO_UUID_VAL());

            final var post1 = createPost(ConstantsKt.getTEST_1_UUID_VAL());
            final var post2 = createPost(ConstantsKt.getTEST_2_UUID_VAL());
            final var post3 = createPost(ConstantsKt.getTEST_3_UUID_VAL());

            final var postIds = List.of(post1.id(), post2.id(), post3.id());

            final var post1Comments = List.of(
                createComment(user, post1)
            );
            final var post2Comments = List.of(
                createComment(user, post2),
                createComment(user, post2)
            );
            final var post3Comments = List.of(
                createComment(user, post3),
                createComment(user, post3),
                createComment(user, post3)
            );

            // When
            final var result = commentRepository.countCommentsByPostIds(postIds);

            // Then
            assertThat(result)
                .isNotEmpty()
                .hasSize(3)
                .containsEntry(post1.id(), post1Comments.size())
                .containsEntry(post2.id(), post2Comments.size())
                .containsEntry(post3.id(), post3Comments.size());
        }

        @Test
        void should_return_empty_map_when_post_ids_is_empty() {
            // Given
            final List<UUID> postIds = List.of();

            // When
            final var result = commentRepository.countCommentsByPostIds(postIds);

            // Then
            assertThat(result)
                .isEmpty();
        }
    }
}
