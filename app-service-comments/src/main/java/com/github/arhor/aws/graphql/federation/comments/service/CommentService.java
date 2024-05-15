package com.github.arhor.aws.graphql.federation.comments.service;

import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.Comment;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.CreateCommentInput;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.CreateCommentResult;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.DeleteCommentInput;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.DeleteCommentResult;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.UpdateCommentInput;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.UpdateCommentResult;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface CommentService {

    Map<UUID, List<Comment>> getCommentsByUserIds(Collection<UUID> userIds);

    Map<UUID, List<Comment>> getCommentsByPostIds(Collection<UUID> postIds);

    CreateCommentResult createComment(CreateCommentInput input);

    UpdateCommentResult updateComment(UpdateCommentInput input);

    DeleteCommentResult deleteComment(DeleteCommentInput input);
}
