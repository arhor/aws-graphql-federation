package com.github.arhor.aws.graphql.federation.posts.data.repository.mapping

import com.github.arhor.aws.graphql.federation.posts.data.repository.mapping.PostIdToTagNamesResultSetExtractor.Companion.SELECT_COL_POST_ID
import com.github.arhor.aws.graphql.federation.posts.data.repository.mapping.PostIdToTagNamesResultSetExtractor.Companion.SELECT_COL_TAGS
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.sql.ResultSet
import java.util.UUID

internal class PostIdToTagNamesResultSetExtractorTest {

    private val resultSetExtractor = PostIdToTagNamesResultSetExtractor()

    @Test
    fun `should return map with 2 entries each with 3 expected tags`() {
        // Given
        val post1Id = UUID.randomUUID()
        val post2Id = UUID.randomUUID()

        val expectedTags = Array(3) { "test-$it" }
        val resultSet = mockk<ResultSet> {
            every { next() } returns true andThen true andThen false
            every { getObject(any<String>(), UUID::class.java) } returns post1Id andThen post2Id
            every { getArray(any<String>()) } returns mockk {
                every { array } returns expectedTags
            }
        }

        // When
        val result = resultSetExtractor.extractData(resultSet)

        // Then
        assertThat(result)
            .isNotEmpty()
            .hasSize(2)
            .hasEntrySatisfying(post1Id) { assertThat(it).containsExactly(*expectedTags) }
            .hasEntrySatisfying(post2Id) { assertThat(it).containsExactly(*expectedTags) }

        verify(exactly = 2) { resultSet.getObject(SELECT_COL_POST_ID, UUID::class.java) }
        verify(exactly = 2) { resultSet.getArray(SELECT_COL_TAGS) }
    }
}
