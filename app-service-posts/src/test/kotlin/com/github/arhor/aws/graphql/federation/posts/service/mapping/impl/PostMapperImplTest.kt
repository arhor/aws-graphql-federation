package com.github.arhor.aws.graphql.federation.posts.service.mapping.impl

import com.github.arhor.aws.graphql.federation.posts.data.entity.PostEntity
import com.github.arhor.aws.graphql.federation.posts.data.entity.TagRef
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.CreatePostInput
import com.github.arhor.aws.graphql.federation.starter.testing.OMNI_UUID_VAL
import com.github.arhor.aws.graphql.federation.starter.testing.ZERO_UUID_VAL
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.from
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl

class PostMapperImplTest {

    private val postMapper = PostMapperImpl()

    @Nested
    @DisplayName("PostMapper :: mapToEntity")
    inner class MapToEntityTest {
        @Test
        fun `should correctly map CreatePostInput with empty tags to PostEntity`() {
            // Given
            val createPostInput = CreatePostInput(
                title = "test-title",
                content = "test-content",
                tags = emptyList()
            )

            val expectedTagRefs = emptySet<TagRef>()

            // When
            val entity = postMapper.mapToEntity(createPostInput, USER_ID, expectedTagRefs)

            // Then
            assertThat(entity)
                .isNotNull()
                .returns(USER_ID, from { it.userId })
                .returns(createPostInput.title, from { it.title })
                .returns(createPostInput.content, from { it.content })
                .returns(expectedTagRefs, from { it.tags })
        }

        @Test
        fun `should correctly map CreatePostInput with null tags to PostEntity`() {
            // Given
            val createPostInput = CreatePostInput(
                title = "test-title",
                content = "test-content",
                tags = null
            )

            val expectedTagRefs = emptySet<TagRef>()

            // When
            val entity = postMapper.mapToEntity(createPostInput, USER_ID, null)

            // Then
            assertThat(entity)
                .isNotNull()
                .returns(USER_ID, from { it.userId })
                .returns(createPostInput.title, from { it.title })
                .returns(createPostInput.content, from { it.content })
                .returns(expectedTagRefs, from { it.tags })
        }
    }

    @Nested
    @DisplayName("PostMapper :: mapToPost")
    inner class MapToPostTest {
        @Test
        fun `should correctly map PostEntity to Post`() {
            // Given
            val entity = PostEntity(
                id = POST_ID,
                userId = USER_ID,
                title = "test-title",
                content = "test-content",
            )

            // When
            val result = postMapper.mapToPost(entity)

            // Then
            assertThat(result)
                .isNotNull()
                .returns(entity.id, from { it.id })
                .returns(entity.userId, from { it.userId })
                .returns(entity.title, from { it.title })
                .returns(entity.content, from { it.content })
        }
    }

    @Nested
    @DisplayName("PostMapper :: mapToPostPageFromEntity")
    inner class MapToPostPageFromEntityTest {
        @Test
        fun `should correctly map PostEntity to PostPage`() {
            // Given
            val entity = PostEntity(
                id = POST_ID,
                userId = USER_ID,
                title = "test-title",
                content = "test-content",
            )
            val page = PageImpl(listOf(entity))

            // When
            val result = postMapper.mapToPostPageFromEntity(page)

            // Then
            assertThat(result.data)
                .isNotNull()
                .singleElement()
                .returns(entity.id, from { it.id })
                .returns(entity.userId, from { it.userId })
                .returns(entity.title, from { it.title })
                .returns(entity.content, from { it.content })
        }
    }

    companion object {
        private val USER_ID = ZERO_UUID_VAL
        private val POST_ID = OMNI_UUID_VAL
    }
}
