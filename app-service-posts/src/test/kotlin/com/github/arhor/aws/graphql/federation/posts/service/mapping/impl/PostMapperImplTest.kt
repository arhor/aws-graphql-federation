package com.github.arhor.aws.graphql.federation.posts.service.mapping.impl

import com.github.arhor.aws.graphql.federation.posts.data.entity.PostEntity
import com.github.arhor.aws.graphql.federation.posts.data.entity.TagEntity
import com.github.arhor.aws.graphql.federation.posts.data.entity.TagRef
import com.github.arhor.aws.graphql.federation.posts.data.entity.projection.PostProjection
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.CreatePostInput
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.Option
import com.github.arhor.aws.graphql.federation.posts.service.mapping.OptionsMapper
import com.github.arhor.aws.graphql.federation.posts.service.mapping.TagMapper
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.from
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.UUID

class PostMapperImplTest {

    private val optionsMapper = mockk<OptionsMapper>()
    private val tagMapper = mockk<TagMapper>()

    private val postMapper = PostMapperImpl(
        optionsMapper,
        tagMapper,
    )

    @AfterEach
    fun tearDown() {
        confirmVerified(optionsMapper, tagMapper)
    }

    @Nested
    @DisplayName("PostMapper :: mapToEntity")
    inner class MapToEntityTest {
        @Test
        fun `should correctly map CreatePostInput to PostEntity calling options and tags mappers`() {
            // Given
            val createPostInput = CreatePostInput(
                userId = UUID.randomUUID(),
                title = "test-title",
                content = "test-content",
            )
            val tags = emptySet<TagEntity>()

            val expectedOptions = PostEntity.Options()
            val expectedTagRefs = emptySet<TagRef>()

            every { optionsMapper.mapFromList(any()) } returns expectedOptions
            every { tagMapper.mapToRefs(any()) } returns expectedTagRefs

            // When
            val entity = postMapper.mapToEntity(createPostInput, tags)

            // Then
            verify(exactly = 1) { optionsMapper.mapFromList(createPostInput.options) }
            verify(exactly = 1) { tagMapper.mapToRefs(tags) }

            assertThat(entity)
                .isNotNull()
                .returns(createPostInput.userId, from { it.userId })
                .returns(createPostInput.title, from { it.title })
                .returns(createPostInput.content, from { it.content })
                .returns(expectedOptions, from { it.options })
                .returns(expectedTagRefs, from { it.tags })
        }
    }

    @Nested
    @DisplayName("PostMapper :: mapToPost")
    inner class MapToPostTest {
        @Test
        fun `should correctly map PostEntity to Post calling options mapper`() {
            // Given
            val entity = PostEntity(
                id = UUID.randomUUID(),
                userId = UUID.randomUUID(),
                title = "test-title",
                content = "test-content",
            )
            val expectedOptions = emptyList<Option>()

            every { optionsMapper.mapIntoList(any()) } returns expectedOptions

            // When
            val result = postMapper.mapToPost(entity)

            // Then
            verify(exactly = 1) { optionsMapper.mapIntoList(entity.options) }

            assertThat(result)
                .isNotNull()
                .returns(entity.id, from { it.id })
                .returns(entity.userId, from { it.userId })
                .returns(entity.title, from { it.title })
                .returns(entity.content, from { it.content })
                .returns(expectedOptions, from { it.options })
        }

        @Test
        fun `should correctly map PostProjection to Post calling options mapper`() {
            // Given
            val projection = PostProjection(
                id = UUID.randomUUID(),
                userId = UUID.randomUUID(),
                title = "test-title",
                content = "test-content",
                options = PostEntity.Options()
            )
            val expectedOptions = emptyList<Option>()

            every { optionsMapper.mapIntoList(any()) } returns expectedOptions

            // When
            val result = postMapper.mapToPost(projection)

            // Then
            verify(exactly = 1) { optionsMapper.mapIntoList(projection.options) }

            assertThat(result)
                .isNotNull()
                .returns(projection.id, from { it.id })
                .returns(projection.userId, from { it.userId })
                .returns(projection.title, from { it.title })
                .returns(projection.content, from { it.content })
                .returns(expectedOptions, from { it.options })
        }
    }
}
