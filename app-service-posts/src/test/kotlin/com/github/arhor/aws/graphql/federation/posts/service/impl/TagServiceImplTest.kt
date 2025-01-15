package com.github.arhor.aws.graphql.federation.posts.service.impl

import com.github.arhor.aws.graphql.federation.posts.data.repository.TagRepository
import com.github.arhor.aws.graphql.federation.starter.testing.TEST_1_UUID_VAL
import com.github.arhor.aws.graphql.federation.starter.testing.TEST_2_UUID_VAL
import com.github.arhor.aws.graphql.federation.starter.testing.TEST_3_UUID_VAL
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.UUID

class TagServiceImplTest {

    private val tagRepository = mockk<TagRepository>()

    private val tagService = TagServiceImpl(
        tagRepository,
    )

    @AfterEach
    fun `confirm that all interactions with mocked dependencies were verified`() {
        confirmVerified(tagRepository)
    }

    @Nested
    @DisplayName("Method getTagsByPostIds")
    inner class GetTagsByPostIdsTest {
        @Test
        fun `should return expected map when postIds is not empty calling TagRepository`() {
            // Given
            val postIds = setOf(TEST_1_UUID_VAL, TEST_2_UUID_VAL, TEST_3_UUID_VAL)
            val expectedResult = postIds.associateWith { listOf("test-tag-$it") }

            every { tagRepository.findAllByPostIdIn(any()) } returns expectedResult

            // When
            val result = tagService.getTagsByPostIds(postIds)

            // Then
            verify(exactly = 1) { tagRepository.findAllByPostIdIn(postIds) }

            assertThat(result)
                .isEqualTo(expectedResult)
        }

        @Test
        fun `should return empty map when postIds is empty without calls to TagRepository`() {
            // Given
            val postIds = emptySet<UUID>()

            // When
            val result = tagService.getTagsByPostIds(postIds)

            // Then
            assertThat(result)
                .isEmpty()
        }
    }
}
