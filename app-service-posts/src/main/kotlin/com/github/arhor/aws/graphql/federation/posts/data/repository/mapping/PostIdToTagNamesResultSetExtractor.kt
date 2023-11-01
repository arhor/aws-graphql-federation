package com.github.arhor.aws.graphql.federation.posts.data.repository.mapping

import org.springframework.jdbc.core.ResultSetExtractor
import org.springframework.stereotype.Component
import java.sql.ResultSet

@Component
class PostIdToTagNamesResultSetExtractor : ResultSetExtractor<Map<Long, List<String>>> {

    override fun extractData(rs: ResultSet): Map<Long, List<String>> {
        val result = HashMap<Long, List<String>>()
        while (rs.next()) {
            val post = rs.getLong(SELECT_COL_POST_ID)
            val tags = rs.getArray(SELECT_COL_TAGS)

            result[post] = (tags.array as Array<*>).mapNotNull { it.toString() }
        }
        return result
    }

    companion object {
        const val BEAN_NAME = "postIdToTagNamesResultSetExtractor"

        private const val SELECT_COL_POST_ID = "post_id"
        private const val SELECT_COL_TAGS = "tags"
    }
}
