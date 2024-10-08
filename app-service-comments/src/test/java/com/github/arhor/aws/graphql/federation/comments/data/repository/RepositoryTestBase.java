package com.github.arhor.aws.graphql.federation.comments.data.repository;

import com.github.arhor.aws.graphql.federation.comments.config.ConfigureDatabase;
import com.github.arhor.aws.graphql.federation.comments.data.model.CommentEntity;
import com.github.arhor.aws.graphql.federation.comments.data.model.PostRepresentation;
import com.github.arhor.aws.graphql.federation.comments.data.model.PostRepresentation.PostFeature;
import com.github.arhor.aws.graphql.federation.comments.data.model.PostRepresentation.PostFeatures;
import com.github.arhor.aws.graphql.federation.comments.data.model.UserRepresentation;
import com.github.arhor.aws.graphql.federation.comments.data.model.UserRepresentation.UserFeature;
import com.github.arhor.aws.graphql.federation.comments.data.model.UserRepresentation.UserFeatures;
import com.github.arhor.aws.graphql.federation.starter.core.CoreComponentsAutoConfiguration;
import com.github.arhor.aws.graphql.federation.starter.testing.ConfigureTestObjectMapper;
import org.junit.jupiter.api.Tag;
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

import java.util.EnumSet;
import java.util.UUID;

@Tag("integration")
@DataJdbcTest
@DirtiesContext
@Testcontainers(disabledWithoutDocker = true)
@ContextConfiguration(
    classes = {
        CoreComponentsAutoConfiguration.class,
        ConfigureDatabase.class,
        ConfigureTestObjectMapper.class,
    }
)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
abstract class RepositoryTestBase {

    @Container
    private static final PostgreSQLContainer<?> db = new PostgreSQLContainer<>("postgres:13-alpine");

    @Autowired
    protected CommentRepository commentRepository;

    @Autowired
    protected UserRepresentationRepository userRepository;

    @Autowired
    protected PostRepresentationRepository postRepository;

    @DynamicPropertySource
    public static void registerDynamicProperties(final DynamicPropertyRegistry registry) {
        com.github.arhor.aws.graphql.federation.starter.testing.ConstantsKt.getZERO_UUID_VAL();

        registry.add("spring.datasource.url", db::getJdbcUrl);
        registry.add("spring.datasource.username", db::getUsername);
        registry.add("spring.datasource.password", db::getPassword);
    }

    protected UserRepresentation createUser(final UUID id) {
        return userRepository.save(
            UserRepresentation.builder()
                .id(id)
                .shouldBePersisted(true)
                .build()
        );
    }

    protected UserRepresentation createUser(final UUID id, final UserFeature feature, final UserFeature... features) {
        return userRepository.save(
            UserRepresentation.builder()
                .id(id)
                .features(new UserFeatures(EnumSet.of(feature, features)))
                .shouldBePersisted(true)
                .build()
        );
    }

    protected PostRepresentation createPost(final UUID id) {
        return postRepository.save(
            PostRepresentation.builder()
                .id(id)
                .shouldBePersisted(true)
                .build()
        );
    }

    protected PostRepresentation createPost(final UUID id, final PostFeature feature, final PostFeature... features) {
        return postRepository.save(
            PostRepresentation.builder()
                .id(id)
                .features(new PostFeatures(feature, features))
                .shouldBePersisted(true)
                .build()
        );
    }

    protected CommentEntity createComment(
        final UserRepresentation user,
        final PostRepresentation post
    ) {
        return createComment(user, post, null);
    }

    protected CommentEntity createComment(
        final UserRepresentation user,
        final PostRepresentation post,
        final CommentEntity parent
    ) {
        return commentRepository.save(
            CommentEntity.builder()
                .userId(user.id())
                .postId(post.id())
                .prntId((parent != null) ? parent.id() : null)
                .content("user-" + user.id() + " / post-" + post.id())
                .build()
        );
    }
}
