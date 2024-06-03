package com.github.arhor.aws.graphql.federation.posts.service.mapping.impl

import com.github.arhor.aws.graphql.federation.posts.data.entity.PostEntity
import com.github.arhor.aws.graphql.federation.posts.data.entity.TagEntity
import com.github.arhor.aws.graphql.federation.posts.data.entity.TagRef
import com.github.arhor.aws.graphql.federation.posts.data.entity.projection.PostProjection
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.CreatePostInput
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.from
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.UUID

class PostMapperImplTest {

    private val postMapper = PostMapperImpl()

    @Nested
    @DisplayName("PostMapper :: mapToEntity")
    inner class MapToEntityTest {
        @Test
        fun `should correctly map CreatePostInput to PostEntity calling tags mapper`() {
            // Given
            val createPostInput = CreatePostInput(
                userId = UUID.randomUUID(),
                title = "test-title",
                content = "test-content",
            )
            val tags = emptySet<TagEntity>()

            val expectedTagRefs = emptySet<TagRef>()

            // When
            val entity = postMapper.mapToEntity(createPostInput, tags)

            // Then
            assertThat(entity)
                .isNotNull()
                .returns(createPostInput.userId, from { it.userId })
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
                id = UUID.randomUUID(),
                userId = UUID.randomUUID(),
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

        @Test
        fun `should correctly map PostProjection to Post`() {
            // Given
            val projection = PostProjection(
                id = UUID.randomUUID(),
                userId = UUID.randomUUID(),
                title = "test-title",
                content = "test-content",
            )

            // When
            val result = postMapper.mapToPost(projection)

            // Then
            assertThat(result)
                .isNotNull()
                .returns(projection.id, from { it.id })
                .returns(projection.userId, from { it.userId })
                .returns(projection.title, from { it.title })
                .returns(projection.content, from { it.content })
        }
    }
}
