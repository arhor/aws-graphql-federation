package com.github.arhor.aws.graphql.federation.comments.data.repository;

import com.github.arhor.aws.graphql.federation.comments.config.ConfigureDatabase;
import com.github.arhor.aws.graphql.federation.comments.data.entity.CommentEntity;
import com.github.arhor.aws.graphql.federation.comments.data.entity.PostEntity;
import com.github.arhor.aws.graphql.federation.comments.data.entity.UserEntity;
import com.github.arhor.aws.graphql.federation.comments.data.entity.callback.CommentEntityCallback;
import com.github.arhor.aws.graphql.federation.comments.test.ConfigureTestObjectMapper;
import com.github.arhor.aws.graphql.federation.spring.core.ConfigureCoreApplicationComponents;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DataJdbcTest
@DirtiesContext
@Testcontainers(disabledWithoutDocker = true)
@ContextConfiguration(
    classes = {
        CommentEntityCallback.class,
        ConfigureCoreApplicationComponents.class,
        ConfigureDatabase.class,
        ConfigureTestObjectMapper.class,
    }
)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CommentRepositoryTest {

    @Container
    private static final PostgreSQLContainer<?> db = new PostgreSQLContainer<>("postgres:12-alpine");

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @DynamicPropertySource
    public static void registerDynamicProperties(final DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", db::getJdbcUrl);
        registry.add("spring.datasource.username", db::getUsername);
        registry.add("spring.datasource.password", db::getPassword);
    }

    @Test
    void should_return_expected_list_of_comments_by_user_ids() {
        // Given
        final var user1 = createUser();
        final var user2 = createUser();
        final var user3 = createUser();

        final var post1 = createPost();

        var commentsCounter = 0;

        final var user1Comments = List.of(
            createComment(user1, post1, ++commentsCounter),
            createComment(user1, post1, ++commentsCounter)
        );
        final var user2Comments = List.of(
            createComment(user2, post1, ++commentsCounter),
            createComment(user2, post1, ++commentsCounter)
        );
        final var user3Comments = List.of(
            createComment(user3, post1, ++commentsCounter),
            createComment(user3, post1, ++commentsCounter)
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
        final var user1 = createUser();

        final var post1 = createPost();
        final var post2 = createPost();
        final var post3 = createPost();

        var commentsCounter = 0;

        final var post1Comments = List.of(
            createComment(user1, post1, ++commentsCounter),
            createComment(user1, post1, ++commentsCounter)
        );
        final var post2Comments = List.of(
            createComment(user1, post2, ++commentsCounter),
            createComment(user1, post2, ++commentsCounter)
        );
        final var post3Comments = List.of(
            createComment(user1, post3, ++commentsCounter),
            createComment(user1, post3, ++commentsCounter)
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

    private UserEntity createUser() {
        return userRepository.save(
            UserEntity.builder()
                .id(UUID.randomUUID())
                .build()
        );
    }

    private PostEntity createPost() {
        return postRepository.save(
            PostEntity.builder()
                .id(UUID.randomUUID())
                .build()
        );
    }

    private CommentEntity createComment(final UserEntity user, final PostEntity post, final int num) {
        return commentRepository.save(
            CommentEntity.builder()
                .userId(user.id())
                .postId(post.id())
                .content("user-" + user.id() + " / post-" + post.id() + " / comment-" + num)
                .build()
        );
    }
}
