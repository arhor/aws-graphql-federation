package com.github.arhor.dgs.users.api.graphql

import com.github.arhor.aws.graphql.federation.common.exception.EntityDuplicateException
import com.github.arhor.aws.graphql.federation.common.exception.EntityNotFoundException
import com.netflix.graphql.dgs.exceptions.DefaultDataFetcherExceptionHandler
import com.netflix.graphql.dgs.exceptions.DgsException
import com.netflix.graphql.types.errors.TypedGraphQLError
import graphql.execution.DataFetcherExceptionHandler
import graphql.execution.DataFetcherExceptionHandlerParameters
import graphql.execution.DataFetcherExceptionHandlerResult
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionException

private typealias DFEHandler = DataFetcherExceptionHandler
private typealias DFEHParams = DataFetcherExceptionHandlerParameters
private typealias DFEHResult = DataFetcherExceptionHandlerResult

@Component
class GlobalDataFetchingExceptionHandler(private val delegate: DFEHandler) : DFEHandler by delegate {

    @Autowired
    constructor() : this(delegate = DefaultDataFetcherExceptionHandler())

    override fun handleException(params: DFEHParams): CompletableFuture<DFEHResult> = with(params) {
        when (val throwable = exception.unwrap()) {
            is EntityNotFoundException -> {
                handleEntityNotFoundException(throwable, params)
            }

            is EntityDuplicateException -> {
                handleEntityDuplicateException(throwable, params)
            }

            else -> {
                delegate.handleException(this)
            }
        }
    }

    private fun Throwable.unwrap(): Throwable =
        when {
            this is CompletionException && cause != null -> cause!!
            else -> this
        }

    private fun handleEntityNotFoundException(exception: EntityNotFoundException, params: DFEHParams) =
        TypedGraphQLError
            .newNotFoundBuilder()
            .createResult(exception, params)

    private fun handleEntityDuplicateException(exception: EntityDuplicateException, params: DFEHParams) =
        TypedGraphQLError
            .newConflictBuilder()
            .createResult(exception, params)

    private fun TypedGraphQLError.Builder.createResult(throwable: Throwable, params: DFEHParams) =
        this.message(throwable.message)
            .extensions(mapOf(DgsException.EXTENSION_CLASS_KEY to throwable::class.java.name))
            .also { params.path?.also(::path) }
            .build()
            .let { DFEHResult.newResult(it).build() }
            .let { CompletableFuture.completedFuture(it) }
}
