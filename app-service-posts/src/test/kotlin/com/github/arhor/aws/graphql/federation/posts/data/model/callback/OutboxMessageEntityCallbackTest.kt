package com.github.arhor.aws.graphql.federation.posts.data.model.callback

import com.github.arhor.aws.graphql.federation.posts.data.model.OutboxMessageEntity
import com.github.arhor.aws.graphql.federation.starter.testing.OMNI_UUID_VAL
import com.github.arhor.aws.graphql.federation.starter.testing.ZERO_UUID_VAL
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.from
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class OutboxMessageEntityCallbackTest {

    private val outboxMessageEntityCallback = OutboxMessageEntityCallback()

    @Nested
    @DisplayName("OutboxMessageEntityCallback :: onBeforeConvert")
    inner class OnBeforeConvertTest {
        @Test
        fun `should assign id to the entity if it is missing`() {
            // Given
            val entity =
                OutboxMessageEntity(id = null, type = "test", data = emptyMap(), traceId = TRACE_ID)

            // When
            val result = outboxMessageEntityCallback.onBeforeConvert(entity)

            // Then
            assertThat(result)
                .isNotNull()
                .doesNotReturn(null, from { it.id })
        }

        @Test
        fun `should not re-assign id to the entity if it is already not null`() {
            // Given
            val entity =
                OutboxMessageEntity(id = MESSAGE_ID, type = "test", data = emptyMap(), traceId = TRACE_ID)

            // When
            val result = outboxMessageEntityCallback.onBeforeConvert(entity)

            // Then
            assertThat(result)
                .isNotNull()
                .returns(entity.id, from { it.id })
                .extracting { it.id }
                .isNotNull()
        }
    }

    companion object {
        private val MESSAGE_ID = ZERO_UUID_VAL
        private val TRACE_ID = OMNI_UUID_VAL
    }
}
