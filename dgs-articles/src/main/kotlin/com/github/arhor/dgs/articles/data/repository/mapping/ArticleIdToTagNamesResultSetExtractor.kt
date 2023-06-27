package com.github.arhor.dgs.articles.data.repository.mapping

import com.github.arhor.dgs.articles.data.entity.TagEntity
import com.github.arhor.dgs.articles.data.entity.TagRef
import org.springframework.jdbc.core.ResultSetExtractor
import org.springframework.stereotype.Component
import java.sql.ResultSet

@Component
class ArticleIdToTagNamesResultSetExtractor : ResultSetExtractor<Map<Long, List<String>>> {

    override fun extractData(rs: ResultSet): Map<Long, List<String>> {
        val result = HashMap<Long, ArrayList<String>>()
        while (rs.next()) {
            val articleId = rs.getLong(TagRef.COL_ARTICLE_ID)
            val tagName = rs.getString(TagEntity.COL_NAME)

            result.getOrPut(articleId, ::ArrayList).add(tagName)
        }
        return result
    }

    companion object {
        const val BEAN_NAME = "articleIdToTagNamesResultSetExtractor"
    }
}
