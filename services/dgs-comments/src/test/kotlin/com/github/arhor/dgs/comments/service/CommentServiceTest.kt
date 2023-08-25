package com.github.arhor.dgs.comments.service

import com.github.arhor.dgs.comments.data.repository.CommentRepository
import com.github.arhor.dgs.comments.service.mapper.CommentMapper
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.maps.shouldBeEmpty
import io.mockk.clearAllMocks
import io.mockk.confirmVerified
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.ComponentScan.Filter
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE

class CommentServiceTest(
    @MockkBean private val commentRepository: CommentRepository,
    @MockkBean private val commentMapper: CommentMapper,
    @Autowired private val commentService: CommentService,
) : DescribeSpec({

    describe("getCommentsByUserIds") {
        it("should not interact with repository if userIds argument is empty list") {
            // Given
            val userIds = emptyList<Long>()

            // When
            val result = commentService.getCommentsByUserIds(userIds)

            // Then
            result.shouldBeEmpty()

            confirmVerified(commentRepository, commentMapper)
        }
    }

    afterTest {
        clearAllMocks()
    }
}) {

    @Configuration
    @ComponentScan(
        includeFilters = [Filter(type = ASSIGNABLE_TYPE, classes = [CommentService::class])],
        useDefaultFilters = false,
    )
    class Config
}
