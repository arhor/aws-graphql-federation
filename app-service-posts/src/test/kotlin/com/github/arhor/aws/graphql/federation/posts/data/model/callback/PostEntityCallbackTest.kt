package com.github.arhor.aws.graphql.federation.posts.data.model.callback

import com.github.arhor.aws.graphql.federation.posts.data.model.PostEntity
import com.github.arhor.aws.graphql.federation.starter.testing.ZERO_UUID_VAL
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.from
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class PostEntityCallbackTest {

    private val postEntityCallback = PostEntityCallback()

    @Nested
    @DisplayName("PostEntityCallback :: onBeforeConvert")
    inner class OnBeforeConvertTest {
        @Test
        fun `should assign id to the entity if it is missing`() {
            // Given
            val entity = PostEntity(id = null, userId = null, title = "test", content = "test")

            // When
            val result = postEntityCallback.onBeforeConvert(entity)

            // Then
            assertThat(result)
                .isNotNull()
                .doesNotReturn(null, from { it.id })
        }

        @Test
        fun `should not re-assign id to the entity if it is already not null`() {
            // Given
            val entity = PostEntity(id = ZERO_UUID_VAL, userId = null, title = "test", content = "test")

            // When
            val result = postEntityCallback.onBeforeConvert(entity)

            // Then
            assertThat(result)
                .isNotNull()
                .returns(entity.id, from { it.id })
                .extracting { it.id }
                .isNotNull()
        }
    }
}
