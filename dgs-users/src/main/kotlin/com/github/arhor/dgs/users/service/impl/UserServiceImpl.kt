package com.github.arhor.dgs.users.service.impl

import com.github.arhor.dgs.lib.OffsetBasedPageRequest
import com.github.arhor.dgs.users.Operation
import com.github.arhor.dgs.users.data.repository.UserRepository
import com.github.arhor.dgs.users.generated.graphql.types.CreateUserRequest
import com.github.arhor.dgs.users.generated.graphql.types.UpdateUserRequest
import com.github.arhor.dgs.users.generated.graphql.types.User
import com.github.arhor.dgs.users.service.UserPasswordEncoder
import com.github.arhor.dgs.users.service.UserService
import com.github.arhor.dgs.users.service.mapper.UserMapper
import com.netflix.graphql.dgs.exceptions.DgsEntityNotFoundException
import com.netflix.graphql.dgs.exceptions.DgsException
import com.netflix.graphql.types.errors.ErrorType
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.properties.Delegates

@Service
class UserServiceImpl(
    private val userMapper: UserMapper,
    private val userRepository: UserRepository,
    private val userPasswordEncoder: UserPasswordEncoder,
) : UserService {

    @Transactional
    override fun createUser(request: CreateUserRequest): User {
        if (userRepository.existsByUsername(request.username)) {
            throw object : DgsException(message = "Username already taken", errorType = ErrorType.FAILED_PRECONDITION) {
                val entity = "User"
                val condition = "username = ${request.username}"
                val operation = Operation.CREATE
            }
        }
        return request.copy(password = userPasswordEncoder.encode(request.password))
            .let { userMapper.mapToEntity(it) }
            .let { userRepository.save(it) }
            .let { userMapper.mapToDTO(it) }
    }

    @Transactional
    @Retryable(retryFor = [OptimisticLockingFailureException::class])
    override fun updateUser(request: UpdateUserRequest): User {
        var changed = false
        var user by Delegates.observable(
            initialValue = userRepository.findByIdOrNull(request.id.toLong()) ?: throw object :
                DgsException(message = "UserIsNotFound", errorType = ErrorType.FAILED_PRECONDITION) {
                val entity = "User"
                val condition = "id = ${request.id}"
                val operation = Operation.UPDATE
            },
            onChange = { _, prev, next ->
                if (prev != next) {
                    changed = true
                }
            }
        )
        request.password?.let {
            user = user.copy(password = it)
        }
        request.settings?.let {
            user = user.copy(settings = it)
        }
        if (changed) {
            user = userRepository.save(user)
        }
        return userMapper.mapToDTO(user)
    }

    @Transactional
    override fun deleteUser(userId: Long): Boolean {
        return when (val affected = userRepository.deleteByIdReturningNumberRecordsAffected(userId)) {
            1 -> true
            0 -> false
            else -> throw IllegalStateException("More than 1 user inactivated, but $affected records were affected")
        }
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
