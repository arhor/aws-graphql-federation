package com.github.arhor.dgs.articles.data.repository.mapping

import org.springframework.jdbc.core.ResultSetExtractor
import org.springframework.stereotype.Component
import java.sql.ResultSet

@Component
class ArticleIdToTagNamesResultSetExtractor : ResultSetExtractor<Map<Long, List<String>>> {

    override fun extractData(rs: ResultSet): Map<Long, List<String>> {
        val result = HashMap<Long, ArrayList<String>>()
        while (rs.next()) {
            val articleId = rs.getLong("article_id")
            val tagName = rs.getString("name")

            result.getOrPut(articleId, ::ArrayList).add(tagName)
        }
        return result
    }

    companion object {
        const val BEAN_NAME = "articleIdToTagNamesResultSetExtractor"
    }
}
