package com.github.arhor.dgs.posts.service.impl

import com.github.arhor.dgs.posts.data.entity.UserEntity
import com.github.arhor.dgs.posts.data.repository.UserRepository
import com.github.arhor.dgs.posts.generated.graphql.types.User
import com.github.arhor.dgs.posts.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserServiceImpl @Autowired constructor(
    private val userRepository: UserRepository,
) : UserService {

    override fun getUserById(userId: Long): User? {
        return userRepository.findByIdOrNull(userId)?.let { User(id = it.id) }
    }

    @Transactional
    override fun createUser(userId: Long) {
        userRepository.insert(UserEntity(id = userId))
    }

    @Transactional
    override fun deleteUser(userId: Long) {
        userRepository.deleteById(userId)
    }
}
