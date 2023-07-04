package com.github.arhor.dgs.comments.service.impl

import com.github.arhor.dgs.comments.data.repository.UserRepository
import com.github.arhor.dgs.comments.generated.graphql.types.User
import com.github.arhor.dgs.comments.service.UserService
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(
    private val userRepository: UserRepository
) : UserService {

    override fun getUserById(userId: Long): User {
        TODO("Not yet implemented")
    }

    override fun createUser(userId: Long) {
        TODO("Not yet implemented")
    }

    override fun deleteUser(userId: Long) {
        TODO("Not yet implemented")
    }
}
