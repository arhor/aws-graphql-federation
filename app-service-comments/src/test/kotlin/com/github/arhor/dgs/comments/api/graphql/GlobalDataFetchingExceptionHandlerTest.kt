package com.github.arhor.dgs.comments.api.graphql

import com.github.arhor.dgs.lib.exception.EntityDuplicateException
import com.github.arhor.dgs.lib.exception.EntityNotFoundException
import graphql.execution.DataFetcherExceptionHandler
import graphql.execution.DataFetcherExceptionHandlerParameters
import graphql.schema.DataFetchingEnvironment
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.concurrent.CompletionException
import java.util.stream.Stream

class GlobalDataFetchingExceptionHandlerTest {

    private val mockkDfe = mockk<DataFetchingEnvironment>(relaxed = true)
    private val mockkDelegate = mockk<DataFetcherExceptionHandler>(relaxed = true)

    private val handlerUnderTest = GlobalDataFetchingExceptionHandler(
        delegate = mockkDelegate,
    )

    @MethodSource
    @ParameterizedTest
    fun `should call delegate only for unexpected exceptions`(exception: Exception, expectedDelegateCalls: Int) {
        // Given
        val params =
            DataFetcherExceptionHandlerParameters
                .newExceptionParameters()
                .dataFetchingEnvironment(mockkDfe)
                .exception(exception)
                .build()

        // When
        handlerUnderTest.handleException(params)

        // Then
        verify(exactly = expectedDelegateCalls) { mockkDelegate.handleException(params) }
    }

    companion object {
        @JvmStatic
        fun `should call delegate only for unexpected exceptions`(): Stream<Arguments> = Stream.of(
            // @formatter:off
            arguments( EntityNotFoundException("TEST", "TEST")                      , 0 ),
            arguments( CompletionException(EntityNotFoundException("TEST", "TEST")) , 0 ),
            arguments( EntityDuplicateException("TEST", "TEST")                     , 0 ),
            arguments( CompletionException(EntityDuplicateException("TEST", "TEST")), 0 ),
            arguments( RuntimeException("TEST")                                     , 1 ),
            // @formatter:on
        )
    }
}
