package com.github.arhor.aws.graphql.federation.spring.dgs

import com.github.arhor.aws.graphql.federation.common.exception.EntityDuplicateException
import com.github.arhor.aws.graphql.federation.common.exception.EntityNotFoundException
import com.github.arhor.aws.graphql.federation.common.exception.EntityOperationRestrictedException
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
                onEntityNotFoundException(throwable, params)
            }

            is EntityDuplicateException -> {
                onEntityDuplicateException(throwable, params)
            }

            is EntityOperationRestrictedException -> {
                onEntityOperationRestrictedException(throwable, params)
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

    private fun onEntityNotFoundException(e: EntityNotFoundException, params: DfeHandlerParams) =
        TypedGraphQLError.newNotFoundBuilder()
            .createResult(e, params)

    private fun onEntityDuplicateException(e: EntityDuplicateException, params: DfeHandlerParams) =
        TypedGraphQLError.newConflictBuilder()
            .createResult(e, params)

    private fun onEntityOperationRestrictedException(e: EntityOperationRestrictedException, params: DfeHandlerParams) =
        TypedGraphQLError.newPermissionDeniedBuilder()
            .createResult(e, params)

    private fun TypedGraphQLError.Builder.createResult(throwable: Throwable, params: DfeHandlerParams) =
        this.message(throwable.message)
            .extensions(mapOf(DgsException.EXTENSION_CLASS_KEY to throwable::class.java.name))
            .also { params.path?.also(::path) }
            .build()
            .let { DfeHandlerResult.newResult(it).build() }
            .let { CompletableFuture.completedFuture(it) }
}
