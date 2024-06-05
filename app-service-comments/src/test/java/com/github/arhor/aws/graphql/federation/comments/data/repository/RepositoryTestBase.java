package com.github.arhor.aws.graphql.federation.comments.data.repository;

import com.github.arhor.aws.graphql.federation.comments.config.ConfigureDatabase;
import com.github.arhor.aws.graphql.federation.comments.data.entity.CommentEntity;
import com.github.arhor.aws.graphql.federation.comments.data.entity.HasComments.Feature;
import com.github.arhor.aws.graphql.federation.comments.data.entity.PostRepresentation;
import com.github.arhor.aws.graphql.federation.comments.data.entity.UserRepresentation;
import com.github.arhor.aws.graphql.federation.comments.test.ConfigureTestObjectMapper;
import com.github.arhor.aws.graphql.federation.starter.core.ConfigureCoreApplicationComponents;
import com.github.arhor.aws.graphql.federation.starter.core.data.Features;
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
        registry.add("spring.datasource.url", db::getJdbcUrl);
        registry.add("spring.datasource.username", db::getUsername);
        registry.add("spring.datasource.password", db::getPassword);
    }

    protected UserRepresentation createUser() {
        return userRepository.save(
            UserRepresentation.builder()
                .id(UUID.randomUUID())
                .shouldBePersisted(true)
                .build()
        );
    }

    protected UserRepresentation createUser(final Feature feature,final Feature... features) {
        return userRepository.save(
            UserRepresentation.builder()
                .id(UUID.randomUUID())
                .features(Features.of(feature, features))
                .shouldBePersisted(true)
                .build()
        );
    }

    protected PostRepresentation createPost() {
        return postRepository.save(
            PostRepresentation.builder()
                .id(UUID.randomUUID())
                .shouldBePersisted(true)
                .build()
        );
    }

    protected PostRepresentation createPost(final Feature feature,final Feature... features) {
        return postRepository.save(
            PostRepresentation.builder()
                .id(UUID.randomUUID())
                .features(Features.of(feature, features))
                .shouldBePersisted(true)
                .build()
        );
    }

    protected CommentEntity createComment(
        final UserRepresentation user,
        final PostRepresentation post
    ) {
        return commentRepository.save(
            CommentEntity.builder()
                .userId(user.id())
                .postId(post.id())
                .content("user-" + user.id() + " / post-" + post.id())
                .build()
        );
    }
}
