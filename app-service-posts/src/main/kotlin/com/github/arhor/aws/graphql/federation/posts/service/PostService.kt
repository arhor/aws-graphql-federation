package com.github.arhor.aws.graphql.federation.posts.service

import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.CreatePostInput
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.Post
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.PostPage
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.PostsLookupInput
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.UpdatePostInput
import com.github.arhor.aws.graphql.federation.starter.security.CurrentUserDetails
import java.util.UUID

/**
 * Service interface for handling posts.
 */
interface PostService {

    /**
     * Retrieves a post by its ID.
     *
     * @param id the UUID of the post to retrieve
     * @return the post with the specified ID
     */
    fun getPostById(id: UUID): Post

    /**
     * Retrieves a list of posts based on the provided input criteria.
     *
     * @param input the input object containing the criteria for retrieving posts
     * @return a list of posts matching the criteria
     */
    fun getPostPage(input: PostsLookupInput): PostPage

    /**
     * Retrieves posts for the specified user IDs.
     *
     * @param userIds the set of user IDs to retrieve posts for
     * @return a map where the key is the user ID and the value is a list of posts associated with that user
     */
    fun getPostsByUserIds(userIds: Set<UUID>): Map<UUID, List<Post>>

    /**
     * Creates a new post.
     *
     * @param input the input object containing the necessary data to create a post
     * @param actor the user creating post
     * @return the created post
     */
    fun createPost(input: CreatePostInput, actor: CurrentUserDetails): Post

    /**
     * Updates an existing post.
     *
     * @param input the input object containing the necessary data to update the post
     * @param actor the user updating post
     * @return the updated post
     */
    fun updatePost(input: UpdatePostInput, actor: CurrentUserDetails): Post

    /**
     * Deletes a post.
     *
     * @param id    the id of the post to delete
     * @param actor the user deleting post
     * @return `true` if the post was successfully deleted, `false` otherwise
     */
    fun deletePost(id: UUID, actor: CurrentUserDetails): Boolean

    /**
     * Creates/Deletes like on the post for a specified user.
     *
     * @param postId the id of a post
     * @param actor  the actor trying to create/delete like
     * @return `true` if the posts enabled, `false` otherwise
     */
    fun togglePostLike(postId: UUID, actor: CurrentUserDetails): Boolean
}
