package com.github.arhor.aws.graphql.federation.starter.graphql

import com.netflix.graphql.dgs.context.DgsContext
import com.netflix.graphql.dgs.internal.DgsWebMvcRequestData
import com.netflix.graphql.dgs.internal.method.ArgumentResolver
import graphql.schema.DataFetchingEnvironment
import org.springframework.core.MethodParameter
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver
import org.springframework.web.context.request.NativeWebRequest

class AuthenticationPrincipalDgsArgumentResolver : ArgumentResolver {

    private val delegate = AuthenticationPrincipalArgumentResolver()

    override fun resolveArgument(parameter: MethodParameter, dfe: DataFetchingEnvironment): Any? {
        @Suppress("DEPRECATION")
        val context = dfe.getContext<DgsContext>()
        val reqData = context.requestData

        if (reqData is DgsWebMvcRequestData) {
            val request = reqData.webRequest

            if (request is NativeWebRequest) {
                return delegate.resolveArgument(parameter, null, request, null)
            }
        }
        return null
    }

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return delegate.supportsParameter(parameter)
    }
}
