package com.github.arhor.aws.graphql.federation.comments.service;

import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.Comment;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.CreateCommentInput;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.CreateCommentResult;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.UpdateCommentInput;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.UpdateCommentResult;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface CommentService {

    Map<Long, List<Comment>> getCommentsByUserIds(Collection<Long> userIds);

    Map<Long, List<Comment>> getCommentsByPostIds(Collection<Long> postIds);

    CreateCommentResult createComment(CreateCommentInput input);

    UpdateCommentResult updateComment(UpdateCommentInput input);

    boolean deleteComment(long id);

    void unlinkUserComments(long userId);

    void deletePostComments(long postId);
}
