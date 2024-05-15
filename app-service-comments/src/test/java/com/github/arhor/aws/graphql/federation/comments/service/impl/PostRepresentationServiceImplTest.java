package com.github.arhor.aws.graphql.federation.comments.service.impl;

import com.github.arhor.aws.graphql.federation.comments.data.entity.PostRepresentationEntity;
import com.github.arhor.aws.graphql.federation.comments.data.repository.PostRepresentationRepository;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.DgsConstants.POST;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.Post;
import com.github.arhor.aws.graphql.federation.common.exception.EntityNotFoundException;
import com.github.arhor.aws.graphql.federation.common.exception.Operation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCache;

import java.util.Optional;
import java.util.UUID;

import static com.github.arhor.aws.graphql.federation.comments.util.Caches.IDEMPOTENT_ID_SET;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.assertj.core.api.Assertions.from;
import static org.assertj.core.api.InstanceOfAssertFactories.type;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PostRepresentationServiceImplTest {

    private final Cache cache = new ConcurrentMapCache(IDEMPOTENT_ID_SET.name());
    private final CacheManager cacheManager = mock();
    private final PostRepresentationRepository postRepresentationRepository = mock();

    private PostRepresentationServiceImpl postService;

    @BeforeEach
    void setUp() {
        when(cacheManager.getCache(IDEMPOTENT_ID_SET.name()))
            .thenReturn(cache);

        postService = new PostRepresentationServiceImpl(cacheManager, postRepresentationRepository);
        postService.initialize();
    }


    @Nested
    @DisplayName("PostService :: findPostRepresentation")
    class FindPostRepresentationTest {
        @Test
        void should_return_expected_post_when_it_exists_by_id() {
            // Given
            final var postId = UUID.randomUUID();

            when(postRepresentationRepository.findById(any()))
                .thenReturn(Optional.of(new PostRepresentationEntity(postId)));

            // When
            final var result = postService.findPostRepresentation(postId);

            // Then
            then(postRepresentationRepository)
                .should()
                .findById(postId);

            then(postRepresentationRepository)
                .shouldHaveNoMoreInteractions();

            assertThat(result)
                .isNotNull()
                .returns(postId, from(Post::getId));
        }

        @Test
        void should_throw_EntityNotFoundException_when_post_does_not_exist_by_id() {
            // Given
            final var postId = UUID.randomUUID();

            final var expectedEntity = POST.TYPE_NAME;
            final var expectedCondition = POST.Id + " = " + postId;
            final var expectedOperation = Operation.LOOKUP;

            when(postRepresentationRepository.findById(any()))
                .thenReturn(Optional.empty());

            // When
            final var result = catchException(() -> postService.findPostRepresentation(postId));

            // Then
            then(postRepresentationRepository)
                .should()
                .findById(postId);

            then(postRepresentationRepository)
                .shouldHaveNoMoreInteractions();

            assertThat(result)
                .isNotNull()
                .asInstanceOf(type(EntityNotFoundException.class))
                .returns(expectedEntity, from(EntityNotFoundException::getEntity))
                .returns(expectedCondition, from(EntityNotFoundException::getCondition))
                .returns(expectedOperation, from(EntityNotFoundException::getOperation));
        }
    }

    @Nested
    @DisplayName("PostService :: createPostRepresentation")
    class CreatePostRepresentationTest {
        @Test
        void should_call_postRepository_save_only_once_with_the_same_idempotencyKey() {
            // Given
            final var idempotencyKey = UUID.randomUUID();
            final var postId = UUID.randomUUID();
            final var expectedPost = new PostRepresentationEntity(postId);

            // When
            for (int i = 0; i < 3; i++) {
                postService.createPostRepresentation(postId, idempotencyKey);
            }

            // Then
            then(postRepresentationRepository)
                .should()
                .save(expectedPost);

            then(postRepresentationRepository)
                .shouldHaveNoMoreInteractions();
        }
    }

    @Nested
    @DisplayName("PostService :: deletePostRepresentation")
    class DeletePostRepresentationTest {
        @Test
        void should_call_postRepository_deleteById_only_once_with_the_same_idempotencyKey() {
            // Given
            final var idempotencyKey = UUID.randomUUID();
            final var postId = UUID.randomUUID();

            // When
            for (int i = 0; i < 3; i++) {
                postService.deletePostRepresentation(postId, idempotencyKey);
            }

            // Then
            then(postRepresentationRepository)
                .should()
                .deleteById(postId);

            then(postRepresentationRepository)
                .shouldHaveNoMoreInteractions();
        }
    }
}