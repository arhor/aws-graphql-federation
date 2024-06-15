package com.github.arhor.aws.graphql.federation.comments.data.repository;

import com.github.arhor.aws.graphql.federation.comments.config.ConfigureDatabase;
import com.github.arhor.aws.graphql.federation.comments.data.entity.CommentEntity;
import com.github.arhor.aws.graphql.federation.comments.data.entity.PostRepresentation;
import com.github.arhor.aws.graphql.federation.comments.data.entity.UserRepresentation;
import com.github.arhor.aws.graphql.federation.starter.core.ConfigureCoreApplicationComponents;
import com.github.arhor.aws.graphql.federation.starter.core.data.Features;
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

import java.util.UUID;

@Tag("integration")
@DataJdbcTest
@DirtiesContext
@Testcontainers(disabledWithoutDocker = true)
@ContextConfiguration(
    classes = {
        ConfigureCoreApplicationComponents.class,
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

    protected UserRepresentation createUser(final UUID id, final UserRepresentation.Feature feature, final UserRepresentation.Feature... features) {
        return userRepository.save(
            UserRepresentation.builder()
                .id(id)
                .features(Features.of(feature, features))
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

    protected PostRepresentation createPost(final UUID id, final PostRepresentation.Feature feature, final PostRepresentation.Feature... features) {
        return postRepository.save(
            PostRepresentation.builder()
                .id(id)
                .features(Features.of(feature, features))
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
