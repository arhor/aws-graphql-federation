package com.github.arhor.aws.graphql.federation.users.service

interface AuthService {

    fun getAuthoritiesByUserIds(userIds: Set<Long>): Map<Long, List<String>>
}
