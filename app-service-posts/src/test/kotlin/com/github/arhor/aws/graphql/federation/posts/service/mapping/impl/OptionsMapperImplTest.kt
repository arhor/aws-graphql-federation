package com.github.arhor.aws.graphql.federation.posts.service.mapping.impl

import com.github.arhor.aws.graphql.federation.posts.data.entity.PostEntity
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.Option
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.EnumSet

internal class OptionsMapperImplTest {

    private val optionsMapper = OptionsMapperImpl()

    @Nested
    @DisplayName("OptionsMapper :: mapIntoList")
    inner class MapIntoListTest {
        @Test
        fun `should return empty list when empty Options wrapper is passed`() {
            // Given
            val options = PostEntity.Options(items = emptySet())

            // When
            val result = optionsMapper.mapIntoList(options)

            // Then
            assertThat(result)
                .isEmpty()
        }

        @Test
        fun `should return a list containing expected elements of the passed Options wrapper`() {
            // Given
            val options = PostEntity.Options(items = EnumSet.of(Option.NSFW))

            // When
            val result = optionsMapper.mapIntoList(options)

            // Then
            assertThat(result)
                .isNotEmpty()
                .singleElement()
                .isEqualTo(Option.NSFW)
        }
    }

    @Nested
    @DisplayName("OptionsMapper :: mapFromList")
    inner class MapFromListTest {
        @Test
        fun `should return empty Options wrapper when empty list is passed`() {
            // Given
            val options = emptyList<Option>()

            // When
            val result = optionsMapper.mapFromList(options)

            // Then
            assertThat(result.items)
                .isEmpty()
        }

        @Test
        fun `should return empty Options wrapper when null is passed`() {
            // Given
            val options = null

            // When
            val result = optionsMapper.mapFromList(options)

            // Then
            assertThat(result.items)
                .isEmpty()
        }

        @Test
        fun `should return Options wrapper containing expected elements of the passed list of options`() {
            // Given
            val options = listOf(Option.NSFW)

            // When
            val result = optionsMapper.mapFromList(options)

            // Then
            assertThat(result.items)
                .isNotEmpty()
                .singleElement()
                .isEqualTo(Option.NSFW)
        }
    }
}
