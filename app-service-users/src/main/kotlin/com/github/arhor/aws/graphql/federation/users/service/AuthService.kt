package com.github.arhor.aws.graphql.federation.users.service

import java.util.UUID

interface AuthService {

    fun getAuthoritiesByUserIds(userIds: Set<UUID>): Map<UUID, List<String>>
}
