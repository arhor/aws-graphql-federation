package com.github.arhor.dgs.posts.service

import com.github.arhor.dgs.posts.generated.graphql.types.User

interface UserService {
    fun getUserById(userId: Long): User
    fun createUser(userId: Long)
    fun deleteUser(userId: Long)
}
