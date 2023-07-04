package com.github.arhor.dgs.comments.service

import com.github.arhor.dgs.comments.generated.graphql.types.User

interface UserService {
    fun getUserById(userId: Long): User?
    fun createUser(userId: Long)
    fun deleteUser(userId: Long)
}
