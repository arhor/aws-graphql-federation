package com.github.arhor.aws.graphql.federation.posts.data.entity.callback

import com.github.arhor.aws.graphql.federation.posts.data.entity.TagEntity
import com.github.arhor.aws.graphql.federation.starter.testing.ZERO_UUID_VAL
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.from
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class TagEntityCallbackTest {

    private val tagEntityCallback = TagEntityCallback()

    @Nested
    @DisplayName("TagEntityCallback :: onBeforeConvert")
    inner class OnBeforeConvertTest {
        @Test
        fun `should assign id to the entity if it is missing`() {
            // Given
            val entity = TagEntity(id = null, name = "test")

            // When
            val result = tagEntityCallback.onBeforeConvert(entity)

            // Then
            assertThat(result)
                .isNotNull()
                .doesNotReturn(null, from { it.id })
        }

        @Test
        fun `should not re-assign id to the entity if it is already not null`() {
            // Given
            val entity = TagEntity(id = ZERO_UUID_VAL, name = "test")

            // When
            val result = tagEntityCallback.onBeforeConvert(entity)

            // Then
            assertThat(result)
                .isNotNull()
                .returns(entity.id, from { it.id })
                .extracting { it.id }
                .isNotNull()
        }
    }
}
