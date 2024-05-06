package com.github.arhor.aws.graphql.federation.posts.service.mapping.impl

import com.github.arhor.aws.graphql.federation.posts.data.entity.TagEntity
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchException
import org.assertj.core.api.Assertions.from
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class TagMapperImplTest {

    private val tagMapper = TagMapperImpl()

    @Nested
    @DisplayName("TagMapper :: mapToRef")
    inner class MapToRefTest {

        @Test
        fun `should successfully map TagEntity to TagRef with the same ID`() {
            // Given
            val entity = TagEntity(id = 1L, name = "test-tag")

            // When
            val result = tagMapper.mapToRef(entity)

            // Then
            assertThat(result)
                .returns(entity.id, from { it.tagId.id })
        }

        @Test
        fun `should throw IllegalStateException mapping TagEntity without ID`() {
            // Given
            val entity = TagEntity(id = null, name = "test-tag")

            // When
            val result = catchException { tagMapper.mapToRef(entity) }

            // Then
            assertThat(result)
                .isInstanceOf(IllegalArgumentException::class.java)
        }
    }

    @Nested
    @DisplayName("TagMapper :: mapToRefs")
    inner class MapToRefsTest {
        @Test
        fun `should successfully map a list of TagEntity objects to set of TagRef objects`() {
            // Given
            val entities = (1L..5L).map { TagEntity(id = it, name = "test-tag-$it") }

            // When
            val result = tagMapper.mapToRefs(entities)

            // Then
            assertThat(result)
                .isNotEmpty()
                .hasSameSizeAs(entities)
        }
    }
}
