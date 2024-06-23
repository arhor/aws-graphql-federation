package com.github.arhor.aws.graphql.federation.comments.data.repository.mapping;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component(CommentsNumberByPostIdResultSetExtractor.BEAN_NAME)
public class CommentsNumberByPostIdResultSetExtractor implements ResultSetExtractor<Map<UUID, Integer>> {

    public static final String BEAN_NAME = "commentsNumberByPostIdResultSetExtractor";

    private static final String COL_POST_ID = "post_id";
    private static final String COL_COMMENTS_NUMBER = "comments_number";

    @Override
    public Map<UUID, Integer> extractData(final ResultSet rs) throws SQLException, DataAccessException {
        final var result = new HashMap<UUID, Integer>();
        while (rs.next()) {
            final var postId = rs.getObject(COL_POST_ID, UUID.class);
            final var commentsNumber = rs.getInt(COL_COMMENTS_NUMBER);

            final var prev = result.put(postId, commentsNumber);

            if (prev != null) {
                throw new IllegalStateException("More than one record present for the same post ID: " + postId);
            }
        }
        return result;
    }
}
