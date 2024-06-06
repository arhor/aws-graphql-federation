package com.github.arhor.aws.graphql.federation.comments.data.repository;

import com.github.arhor.aws.graphql.federation.comments.data.entity.CommentEntity;
import com.github.arhor.aws.graphql.federation.comments.data.entity.callback.CommentEntityCallback;
import com.github.arhor.aws.graphql.federation.starter.testing.ConstantsKt;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@ContextConfiguration(
    classes = {
        CommentEntityCallback.class,
    }
)
public class CommentRepositoryTest extends RepositoryTestBase {

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
                .toList()
        );

        // Then
        assertThat(result)
            .isNotNull()
            .containsExactlyElementsOf(expectedComments)
            .doesNotContainAnyElementsOf(user3Comments);
    }

    @Test
    void should_return_expected_list_of_comments_by_post_ids() {
        // Given
        final var user1 = createUser(ConstantsKt.getZERO_UUID_VAL());

        final var post1 = createPost(ConstantsKt.getTEST_1_UUID_VAL());
        final var post2 = createPost(ConstantsKt.getTEST_2_UUID_VAL());
        final var post3 = createPost(ConstantsKt.getTEST_3_UUID_VAL());

        final var post1Comments = List.of(
            createComment(user1, post1),
            createComment(user1, post1)
        );
        final var post2Comments = List.of(
            createComment(user1, post2),
            createComment(user1, post2)
        );
        final var post3Comments = List.of(
            createComment(user1, post3),
            createComment(user1, post3)
        );

        final var expectedComments = Stream.concat(post1Comments.stream(), post2Comments.stream()).toList();

        // When
        final var result = commentRepository.findAllByPostIdIn(
            expectedComments
                .stream()
                .map(CommentEntity::postId)
                .toList()
        );

        // Then
        assertThat(result)
            .isNotNull()
            .containsExactlyElementsOf(expectedComments)
            .doesNotContainAnyElementsOf(post3Comments);
    }
}
