package com.github.arhor.aws.graphql.federation.spring.dgs

import com.netflix.graphql.dgs.DgsDataFetchingEnvironment
import com.netflix.graphql.dgs.internal.DgsWebMvcRequestData
import com.netflix.graphql.dgs.internal.method.ArgumentResolver
import graphql.schema.DataFetchingEnvironment
import org.springframework.core.MethodParameter
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver
import org.springframework.web.context.request.NativeWebRequest

class AuthenticationPrincipalDgsArgumentResolver : ArgumentResolver {

    private val delegate = AuthenticationPrincipalArgumentResolver()

    override fun resolveArgument(parameter: MethodParameter, dfe: DataFetchingEnvironment): Any? {
        if (dfe is DgsDataFetchingEnvironment) {
            val context = dfe.getDgsContext()
            val data = context.requestData

            if (data is DgsWebMvcRequestData) {
                val request = data.webRequest

                if (request is NativeWebRequest) {
                    return delegate.resolveArgument(parameter, null, request, null)
                }
            }
        }
        return null
    }

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return delegate.supportsParameter(parameter)
    }
}
