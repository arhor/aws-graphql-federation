package com.github.arhor.dgs.users.service.impl

import com.github.arhor.dgs.lib.OffsetBasedPageRequest
import com.github.arhor.dgs.users.data.repository.UserRepository
import com.github.arhor.dgs.users.generated.graphql.types.CreateUserRequest
import com.github.arhor.dgs.users.generated.graphql.types.User
import com.github.arhor.dgs.users.service.UserService
import com.github.arhor.dgs.users.service.mapper.UserMapper
import com.netflix.graphql.dgs.exceptions.DgsEntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val userMapper: UserMapper,
) : UserService {

    @Transactional
    override fun createNewUser(request: CreateUserRequest): User {
        return userMapper.mapToEntity(request)
            .let { userRepository.save(it) }
            .let { userMapper.mapToDTO(it) }
    }

    @Transactional(readOnly = true)
    override fun getUserByUsername(username: String): User {
        return userRepository.findByUsername(username)?.let { userMapper.mapToDTO(it) }
            ?: throw DgsEntityNotFoundException()
    }

    @Transactional(readOnly = true)
    override fun getAllUsers(limit: Int, offset: Int): List<User> {
        return userRepository
            .findAll(OffsetBasedPageRequest(limit, offset))
            .map(userMapper::mapToDTO)
            .toList()
    }
}
