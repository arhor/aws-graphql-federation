package com.github.arhor.aws.graphql.federation.posts.service.impl

import com.github.arhor.aws.graphql.federation.posts.data.repository.TagRepository
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

internal class TagServiceImplTest {

    private val tagRepository = mockk<TagRepository>()

    private val tagService = TagServiceImpl(
        tagRepository,
    )

    @AfterEach
    fun tearDown() {
        confirmVerified(tagRepository)
    }

    @Nested
    @DisplayName("TagService :: getTagsByPostIds")
    inner class GetTagsByPostIdsTest {
        @Test
        fun `should return expected map when postIds is not empty calling TagRepository`() {
            // Given
            val postIds = (1..3).map { UUID.randomUUID() }.toSet()
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
