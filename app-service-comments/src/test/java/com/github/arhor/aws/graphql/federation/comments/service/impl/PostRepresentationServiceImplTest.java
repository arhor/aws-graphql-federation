package com.github.arhor.aws.graphql.federation.comments.service.impl;

import com.github.arhor.aws.graphql.federation.comments.data.model.PostRepresentation;
import com.github.arhor.aws.graphql.federation.comments.data.model.PostRepresentation.PostFeature;
import com.github.arhor.aws.graphql.federation.comments.data.model.PostRepresentation.PostFeatures;
import com.github.arhor.aws.graphql.federation.comments.data.repository.PostRepresentationRepository;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.DgsConstants.POST;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.Post;
import com.github.arhor.aws.graphql.federation.common.exception.EntityConditionException;
import com.github.arhor.aws.graphql.federation.common.exception.EntityNotFoundException;
import com.github.arhor.aws.graphql.federation.common.exception.EntityOperationRestrictedException;
import com.github.arhor.aws.graphql.federation.common.exception.Operation;
import com.github.arhor.aws.graphql.federation.starter.security.CurrentUserDetails;
import com.github.arhor.aws.graphql.federation.starter.testing.ConstantsKt;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import static com.github.arhor.aws.graphql.federation.comments.service.impl.PostRepresentationServiceImpl.ROLE_ADMIN_AUTH;
import static com.github.arhor.aws.graphql.federation.starter.testing.MockitoExtKt.withFirstArg;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.assertj.core.api.Assertions.from;
import static org.assertj.core.api.InstanceOfAssertFactories.type;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PostRepresentationServiceImplTest {

    private static CurrentUserDetails TEST_ADMIN;
    private static CurrentUserDetails TEST_USER_1;
    private static CurrentUserDetails TEST_USER_2;
    private static PostRepresentation TEST_POST;

    private final PostRepresentationRepository postRepository = mock();
    private final StateGuard stateGuard = mock();

    private final PostRepresentationServiceImpl postService = new PostRepresentationServiceImpl(
        postRepository,
        stateGuard
    );

    @BeforeAll
    static void setupClass() {
        TEST_ADMIN = new CurrentUserDetails(ConstantsKt.getOMNI_UUID_VAL(), List.of(ROLE_ADMIN_AUTH));
        TEST_USER_1 = new CurrentUserDetails(ConstantsKt.getTEST_1_UUID_VAL(), List.of());
        TEST_USER_2 = new CurrentUserDetails(ConstantsKt.getTEST_2_UUID_VAL(), List.of());
        TEST_POST = new PostRepresentation(ConstantsKt.getZERO_UUID_VAL(), TEST_USER_1.getId());
    }

    @AfterAll
    static void closeClass() {
        TEST_ADMIN = null;
        TEST_USER_1 = null;
        TEST_USER_2 = null;
        TEST_POST = null;
    }

    @Nested
    @DisplayName("Method findPostsRepresentationsInBatch")
    class FindPostRepresentationTest {
        @Test
        void should_return_expected_post_when_it_exists_by_id() {
            // Given
            final var postId = TEST_POST.id();
            final var expectedResult = Map.of(
                postId,
                Post.newBuilder()
                    .id(postId)
                    .commentsDisabled(false)
                    .build()
            );
            final var expectedPostIds = Set.of(postId);

            when(postRepository.findAllById(any()))
                .thenReturn(List.of(TEST_POST));

            // When
            final var result = postService.findPostsRepresentationsInBatch(expectedPostIds);

            // Then
            then(postRepository)
                .should()
                .findAllById(expectedPostIds);

            then(postRepository)
                .shouldHaveNoMoreInteractions();

            assertThat(result)
                .isNotNull()
                .isEqualTo(expectedResult);
        }

        @Test
        void should_return_post_with_commentsOperable_false_when_post_does_not_exist_by_id() {
            // Given
            final var postId = TEST_POST.id();
            final var expectedResult = Map.of(
                postId,
                Post.newBuilder()
                    .id(postId)
                    .build()
            );
            final var expectedPostIds = Set.of(postId);

            when(postRepository.findAllById(any()))
                .thenReturn(Collections.emptyList());

            // When
            final var result = postService.findPostsRepresentationsInBatch(expectedPostIds);

            // Then
            then(postRepository)
                .should()
                .findAllById(expectedPostIds);

            then(postRepository)
                .shouldHaveNoMoreInteractions();

            assertThat(result)
                .isNotNull()
                .isEqualTo(expectedResult);
        }

        @Test
        void should_return_empty_map_when_empty_list_if_ids_provided() {
            // Given
            final var postIds = Collections.<UUID>emptySet();

            // When
            final var result = postService.findPostsRepresentationsInBatch(postIds);

            // Then
            then(postRepository)
                .shouldHaveNoInteractions();

            assertThat(result)
                .isNotNull()
                .isEmpty();
        }
    }

    @Nested
    @DisplayName("Method togglePostComments")
    class SwitchPostCommentsTest {
        private static Stream<Arguments> streamPostAndUsers() {
            return Stream.of(
                arguments(TEST_POST, TEST_USER_1),
                arguments(TEST_POST, TEST_ADMIN)
            );
        }

        static Stream<Arguments> should_disable_post_comments_when_they_were_enabled_and_called_by_owning_user_or_admin() {
            return streamPostAndUsers();
        }

        static Stream<Arguments> should_enable_post_comments_when_they_were_enabled_and_called_by_owning_user_or_admin() {
            return streamPostAndUsers();
        }

        @MethodSource
        @ParameterizedTest
        void should_disable_post_comments_when_they_were_enabled_and_called_by_owning_user_or_admin(
            // Given
            final PostRepresentation post,
            final CurrentUserDetails user
        ) {
            final var postId = post.id();

            given(postRepository.findById(any()))
                .willReturn(Optional.of(post));

            given(postRepository.save(any()))
                .willAnswer(withFirstArg());

            // When
            final var result = postService.togglePostComments(postId, user);

            // Then
            then(postRepository)
                .should()
                .findById(postId);

            then(postRepository)
                .should()
                .save(post.toBuilder().features(post.features().plus(PostFeature.COMMENTS_DISABLED)).build());

            assertThat(result)
                .isFalse();
        }

        @MethodSource
        @ParameterizedTest
        void should_enable_post_comments_when_they_were_enabled_and_called_by_owning_user_or_admin(
            // Given
            final PostRepresentation post,
            final CurrentUserDetails user
        ) {
            // Given
            final var testPost =
                post.toBuilder()
                    .features(new PostFeatures(PostFeature.COMMENTS_DISABLED))
                    .build();
            final var expectedPostToSave =
                testPost.toBuilder()
                    .features(testPost.features().minus(PostFeature.COMMENTS_DISABLED))
                    .build();
            final var postId = testPost.id();

            given(postRepository.findById(any()))
                .willReturn(Optional.of(testPost));

            given(postRepository.save(any()))
                .willAnswer(withFirstArg());

            // When
            final var result = postService.togglePostComments(postId, user);

            // Then
            then(postRepository)
                .should()
                .findById(postId);

            then(postRepository)
                .should()
                .save(expectedPostToSave);

            assertThat(result)
                .isTrue();
        }

        @Test
        void should_throw_EntityNotFoundException_when_there_is_no_post_found_by_the_input_id() {
            // Given
            final var postId = TEST_POST.id();

            given(postRepository.findById(any()))
                .willReturn(Optional.empty());

            // When
            final var result = catchException(() -> postService.togglePostComments(postId, TEST_USER_1));

            // Then
            then(postRepository)
                .should()
                .findById(postId);

            assertThat(result)
                .asInstanceOf(type(EntityNotFoundException.class))
                .returns(POST.TYPE_NAME, from(EntityConditionException::getEntity))
                .returns(POST.Id + " = " + postId, from(EntityConditionException::getCondition))
                .returns(Operation.UPDATE, from(EntityConditionException::getOperation));
        }

        @Test
        void should_throw_EntityOperationRestrictedException_when_user_tries_to_toggle_comments_on_anothers_person_post() {
            // Given
            final var postId = TEST_POST.id();

            given(postRepository.findById(any()))
                .willReturn(Optional.of(TEST_POST));

            // When
            final var result = catchException(() -> postService.togglePostComments(postId, TEST_USER_2));

            // Then
            then(postRepository)
                .should()
                .findById(postId);

            assertThat(result)
                .asInstanceOf(type(EntityOperationRestrictedException.class))
                .returns(POST.TYPE_NAME, from(EntityConditionException::getEntity))
                .returns("Not enough permissions to operate post comments", from(EntityConditionException::getCondition))
                .returns(Operation.UPDATE, from(EntityConditionException::getOperation));
        }
    }
}
