package com.github.arhor.aws.graphql.federation.users.service.impl

import com.github.arhor.aws.graphql.federation.tracing.Trace
import com.github.arhor.aws.graphql.federation.users.data.repository.AuthRepository
import com.github.arhor.aws.graphql.federation.users.service.AuthService
import org.springframework.stereotype.Service
import java.util.UUID

@Trace
@Service
class AuthServiceImpl(
    private val authRepository: AuthRepository,
) : AuthService {

    override fun getAuthoritiesByUserIds(userIds: Set<UUID>): Map<UUID, List<String>> = when {
        userIds.isNotEmpty() -> {
            authRepository.findAllByUserIdIn(userIds)
        }

        else -> {
            emptyMap()
        }
    }
}
