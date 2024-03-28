package com.github.arhor.aws.graphql.federation.spring.dgs

import com.github.arhor.aws.graphql.federation.common.exception.EntityDuplicateException
import com.github.arhor.aws.graphql.federation.common.exception.EntityNotFoundException
import com.netflix.graphql.dgs.exceptions.DefaultDataFetcherExceptionHandler
import com.netflix.graphql.dgs.exceptions.DgsException
import com.netflix.graphql.types.errors.TypedGraphQLError
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionException
import graphql.execution.DataFetcherExceptionHandler as DfeHandler
import graphql.execution.DataFetcherExceptionHandlerParameters as DfeHandlerParams
import graphql.execution.DataFetcherExceptionHandlerResult as DfeHandlerResult

@Component
class GlobalDataFetchingExceptionHandler(private val delegate: DfeHandler) : DfeHandler by delegate {

    @Autowired
    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    constructor() : this(delegate = DefaultDataFetcherExceptionHandler())

    override fun handleException(params: DfeHandlerParams): CompletableFuture<DfeHandlerResult> =
        when (val throwable = params.exception.unwrap()) {
            is EntityNotFoundException -> {
                handleEntityNotFoundException(throwable, params)
            }

            is EntityDuplicateException -> {
                handleEntityDuplicateException(throwable, params)
            }

            else -> {
                delegate.handleException(params)
            }
        }

    private fun Throwable.unwrap(): Throwable =
        when {
            this is CompletionException && cause != null -> cause!!
            else -> this
        }

    private fun handleEntityNotFoundException(exception: EntityNotFoundException, params: DfeHandlerParams) =
        TypedGraphQLError.newNotFoundBuilder()
            .createResult(exception, params)

    private fun handleEntityDuplicateException(exception: EntityDuplicateException, params: DfeHandlerParams) =
        TypedGraphQLError.newConflictBuilder()
            .createResult(exception, params)

    private fun TypedGraphQLError.Builder.createResult(throwable: Throwable, params: DfeHandlerParams) =
        this.message(throwable.message)
            .extensions(mapOf(DgsException.EXTENSION_CLASS_KEY to throwable::class.java.name))
            .also { params.path?.also(::path) }
            .build()
            .let { DfeHandlerResult.newResult(it).build() }
            .let { CompletableFuture.completedFuture(it) }
}
