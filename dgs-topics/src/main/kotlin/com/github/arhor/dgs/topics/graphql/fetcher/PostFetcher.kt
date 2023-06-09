package com.github.arhor.dgs.topics.graphql.fetcher

import com.github.arhor.dgs.topics.generated.graphql.types.CreatePostRequest
import com.github.arhor.dgs.topics.generated.graphql.types.Indentifiable
import com.github.arhor.dgs.topics.generated.graphql.types.Post
import com.github.arhor.dgs.topics.graphql.loader.PostBatchLoader
import com.github.arhor.dgs.topics.service.PostService
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsData
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.InputArgument
import java.util.concurrent.CompletableFuture

@DgsComponent
class PostFetcher(private val postService: PostService) {

    @DgsMutation
    fun createPost(@InputArgument request: CreatePostRequest): Post {
        return postService.createNewPost(request)
    }

    @DgsData(
        parentType = com.github.arhor.dgs.topics.generated.graphql.DgsConstants.USER.TYPE_NAME,
        field = com.github.arhor.dgs.topics.generated.graphql.DgsConstants.USER.Posts
    )
    fun userPosts(dfe: DgsDataFetchingEnvironment): CompletableFuture<List<Post>> {
        return dfe.loadPostsUsing<PostBatchLoader.ForUser>()
    }

    @DgsData(
        parentType = com.github.arhor.dgs.topics.generated.graphql.DgsConstants.TOPIC.TYPE_NAME,
        field = com.github.arhor.dgs.topics.generated.graphql.DgsConstants.TOPIC.Posts
    )
    fun topicPosts(dfe: DgsDataFetchingEnvironment): CompletableFuture<List<Post>> {
        return dfe.loadPostsUsing<PostBatchLoader.ForTopic>()
    }

    private inline fun <reified T> DgsDataFetchingEnvironment.loadPostsUsing(): CompletableFuture<List<Post>>
        where T : PostBatchLoader {

        val loader = getDataLoader<Long, List<Post>>(T::class.java)
        val source = getSource<Indentifiable>()

        return loader.load(source.id.toLong())
    }
}
