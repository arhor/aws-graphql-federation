package com.github.arhor.aws.graphql.federation.users.data.repository.mapping

import com.github.arhor.aws.graphql.federation.starter.testing.TEST_1_UUID_VAL
import com.github.arhor.aws.graphql.federation.starter.testing.TEST_2_UUID_VAL
import com.github.arhor.aws.graphql.federation.users.data.repository.mapping.UserIdToAuthNamesResultSetExtractor.Companion.COL_AUTHORITIES
import com.github.arhor.aws.graphql.federation.users.data.repository.mapping.UserIdToAuthNamesResultSetExtractor.Companion.COL_USER_ID
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.sql.ResultSet
import java.util.UUID

class UserIdToAuthNamesResultSetExtractorTest {

    private val resultSetExtractor = UserIdToAuthNamesResultSetExtractor()

    @Test
    fun `should return map with 2 entries each with 3 expected authorities`() {
        // Given
        val user1Id = TEST_1_UUID_VAL
        val user2Id = TEST_2_UUID_VAL
        val userAuthorities = Array(3) { "auth-$it" }

        val resultSet = mockk<ResultSet> {
            every { next() } returns true andThen true andThen false
            every { getObject(any<String>(), UUID::class.java) } returns user1Id andThen user2Id
            every { getArray(any<String>()) } returns mockk {
                every { array } returns userAuthorities
            }
        }

        // When
        val result = resultSetExtractor.extractData(resultSet)

        // Then
        assertThat(result)
            .isNotEmpty()
            .hasSize(2)
            .hasEntrySatisfying(user1Id) { assertThat(it).containsExactly(*userAuthorities) }
            .hasEntrySatisfying(user2Id) { assertThat(it).containsExactly(*userAuthorities) }

        verify(exactly = 2) { resultSet.getObject(COL_USER_ID, UUID::class.java) }
        verify(exactly = 2) { resultSet.getArray(COL_AUTHORITIES) }
    }
}
