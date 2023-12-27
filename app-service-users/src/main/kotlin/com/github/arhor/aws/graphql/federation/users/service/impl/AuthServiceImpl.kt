package com.github.arhor.aws.graphql.federation.users.service.impl

import com.github.arhor.aws.graphql.federation.users.data.repository.AuthRepository
import com.github.arhor.aws.graphql.federation.users.service.AuthService
import org.springframework.stereotype.Service

@Service
class AuthServiceImpl(
    private val authRepository: AuthRepository,
) : AuthService {

    override fun getAuthoritiesByUserIds(userIds: Set<Long>): Map<Long, List<String>> = when {
        userIds.isNotEmpty() -> {
            authRepository.findAllByUserIdIn(userIds)
        }

        else -> {
            emptyMap()
        }
    }
}
