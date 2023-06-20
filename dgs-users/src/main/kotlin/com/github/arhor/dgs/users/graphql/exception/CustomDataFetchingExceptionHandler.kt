package com.github.arhor.dgs.users.graphql.exception

import com.github.arhor.dgs.lib.exception.CustomGQLException
import com.netflix.graphql.dgs.exceptions.DefaultDataFetcherExceptionHandler
import graphql.execution.DataFetcherExceptionHandler
import graphql.execution.DataFetcherExceptionHandlerParameters
import graphql.execution.DataFetcherExceptionHandlerResult
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionException

private typealias DFEHParams = DataFetcherExceptionHandlerParameters
private typealias DFEHResult = DataFetcherExceptionHandlerResult

@Component
class CustomDataFetchingExceptionHandler(
    private val delegate: DataFetcherExceptionHandler,
) : DataFetcherExceptionHandler by delegate {

    @Autowired(required = true)
    constructor() : this(delegate = DefaultDataFetcherExceptionHandler())

    override fun handleException(handlerParameters: DFEHParams): CompletableFuture<DFEHResult> =
        when (val excn = handlerParameters.exception.unwrap()) {
            is CustomGQLException -> {
                excn.toGraphQlError(handlerParameters.path)
                    .let { DataFetcherExceptionHandlerResult.newResult(it).build() }
                    .let { CompletableFuture.completedFuture(it) }
            }

            else -> {
                delegate.handleException(handlerParameters)
            }
        }

    private fun Throwable.unwrap(): Throwable =
        when {
            this is CompletionException && cause != null -> cause!!
            else -> this
        }
}
