package com.github.arhor.dgs.comments.api.listener

import com.github.arhor.dgs.comments.service.CommentService
import com.ninjasquad.springmockk.MockkBean
import io.awspring.cloud.sqs.operations.SqsOperations
import io.awspring.cloud.test.sqs.SqsTest
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.support.GenericMessage

@SqsTest(PostChangeSqsListener::class)
internal class PostChangeSqsListenerTest : BaseSqsListenerTest() {

    @Autowired
    private lateinit var sqs: SqsOperations

    @MockkBean
    private lateinit var mockCommentService: CommentService

    @Test
    fun `should call deleteCommentsFromPost method on the CommentService on Post Deleted event`() {
        // Given
        val postId = 1L
        val event = PostChangeSqsListener.PostChange.Deleted(id = postId)

        // When
        sqs.send(POST_DELETED_TEST_EVENTS_QUEUE, GenericMessage(event))

        // Then
        verify(exactly = 1, timeout = 3000) { mockCommentService.deleteCommentsFromPost(postId) }
    }
}
