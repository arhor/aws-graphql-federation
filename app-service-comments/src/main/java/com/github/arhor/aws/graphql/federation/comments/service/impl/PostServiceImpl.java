package com.github.arhor.aws.graphql.federation.comments.service.impl;

import com.github.arhor.aws.graphql.federation.comments.data.entity.PostEntity;
import com.github.arhor.aws.graphql.federation.comments.data.repository.PostRepository;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.DgsConstants.POST;
import com.github.arhor.aws.graphql.federation.comments.generated.graphql.types.Post;
import com.github.arhor.aws.graphql.federation.comments.service.PostService;
import com.github.arhor.aws.graphql.federation.common.exception.EntityNotFoundException;
import com.github.arhor.aws.graphql.federation.common.exception.Operation;
import com.github.arhor.aws.graphql.federation.tracing.Trace;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Trace
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    @Override
    public Post findInternalPostRepresentation(final UUID postId) {
        return postRepository.findById(postId)
            .map(this::mapEntityToPost)
            .orElseThrow(() -> new EntityNotFoundException(
                POST.TYPE_NAME,
                POST.Id + " = " + postId,
                Operation.LOOKUP
            ));
    }

    @Override
    public void createInternalPostRepresentation(final UUID postId) {
        postRepository.save(
            PostEntity.builder()
                .id(postId)
                .build()
        );
    }

    @Override
    public void deleteInternalPostRepresentation(final UUID postId) {
        postRepository.deleteById(postId);
    }

    private Post mapEntityToPost(final PostEntity entity) {
        return Post.newBuilder()
            .id(entity.id())
            .build();
    }
}
