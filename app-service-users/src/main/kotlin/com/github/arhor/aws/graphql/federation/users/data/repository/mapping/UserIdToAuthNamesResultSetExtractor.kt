package com.github.arhor.aws.graphql.federation.users.data.repository.mapping

import org.springframework.jdbc.core.ResultSetExtractor
import org.springframework.stereotype.Component
import java.sql.ResultSet
import java.util.UUID

@Component
class UserIdToAuthNamesResultSetExtractor : ResultSetExtractor<Map<UUID, List<String>>> {

    override fun extractData(rs: ResultSet): Map<UUID, List<String>> {
        val result = HashMap<UUID, List<String>>()
        while (rs.next()) {
            val userId = rs.getObject(COL_USER_ID, UUID::class.java)
            val authorities = rs.getArray(COL_AUTHORITIES)

            result[userId] = (authorities.array as Array<*>).mapNotNull { it?.toString() }
        }
        return result
    }

    companion object {
        const val BEAN_NAME = "userIdToAuthNamesResultSetExtractor"

        // @formatter:off
        const val COL_USER_ID     = "user_id"
        const val COL_AUTHORITIES = "authorities"
        // @formatter:on
    }
}
