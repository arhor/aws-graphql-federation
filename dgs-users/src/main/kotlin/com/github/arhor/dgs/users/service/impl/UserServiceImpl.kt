package com.github.arhor.dgs.users.service.impl

import com.github.arhor.dgs.lib.OffsetBasedPageRequest
import com.github.arhor.dgs.lib.exception.EntityDuplicateException
import com.github.arhor.dgs.lib.exception.EntityNotFoundException
import com.github.arhor.dgs.lib.exception.Operation
import com.github.arhor.dgs.users.data.repository.UserRepository
import com.github.arhor.dgs.users.generated.graphql.DgsConstants.USER
import com.github.arhor.dgs.users.generated.graphql.types.CreateUserInput
import com.github.arhor.dgs.users.generated.graphql.types.Setting
import com.github.arhor.dgs.users.generated.graphql.types.UpdateUserInput
import com.github.arhor.dgs.users.generated.graphql.types.User
import com.github.arhor.dgs.users.service.UserMapper
import com.github.arhor.dgs.users.service.UserService
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.retry.annotation.Retryable
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.EnumSet
import kotlin.properties.Delegates

@Service
class UserServiceImpl(
    private val userMapper: UserMapper,
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) : UserService {

    @Transactional
    override fun createUser(input: CreateUserInput): User {
        if (userRepository.existsByUsername(input.username)) {
            throw EntityDuplicateException(
                entity = USER.TYPE_NAME,
                condition = "${USER.Username} = ${input.username}",
                operation = Operation.CREATE,
            )
        }
        return input.copy(password = passwordEncoder.encode(input.password))
            .let { userMapper.mapToEntity(it) }
            .let { userRepository.save(it) }
            .let { userMapper.mapToDTO(it) }
    }

    @Transactional
    @Retryable(retryFor = [OptimisticLockingFailureException::class])
    override fun updateUser(input: UpdateUserInput): User {
        var changed = false
        var user by Delegates.observable(
            initialValue = userRepository.findByIdOrNull(input.id) ?: throw EntityNotFoundException(
                entity = USER.TYPE_NAME,
                condition = "${USER.Id} = ${input.id}",
                operation = Operation.UPDATE,
            ),
            onChange = { _, prev, next ->
                if (prev != next) {
                    changed = true
                }
            }
        )
        input.password?.let {
            user = user.copy(password = passwordEncoder.encode(it))
        }
        input.settings?.let {
            user = user.copy(settings = EnumSet.noneOf(Setting::class.java).apply { addAll(it) })
        }
        if (changed) {
            user = userRepository.save(user)
        }
        return userMapper.mapToDTO(user)
    }

    @Transactional
    override fun deleteUser(id: Long): Boolean {
        return when (val affected = userRepository.deleteByIdReturningNumberRecordsAffected(id)) {
            1 -> true
            0 -> false
            else -> throw EntityDuplicateException(
                entity = USER.TYPE_NAME,
                condition = "${USER.Id} = $id",
                operation = Operation.DELETE,
                cause = IllegalStateException("More than 1 user inactivated, but $affected records were affected")
            )
        }
    }

    @Transactional(readOnly = true)
    override fun getUserById(id: Long): User {
        return userRepository.findByIdOrNull(id)?.let { userMapper.mapToDTO(it) }
            ?: throw EntityNotFoundException(
                entity = USER.TYPE_NAME,
                condition = "${USER.Id} = $id",
                operation = Operation.READ,
            )
    }

    @Transactional(readOnly = true)
    override fun getUserByUsername(username: String): User {
        return userRepository.findByUsername(username)?.let { userMapper.mapToDTO(it) }
            ?: throw EntityNotFoundException(
                entity = USER.TYPE_NAME,
                condition = "${USER.Username} = $username",
                operation = Operation.READ,
            )
    }

    @Transactional(readOnly = true)
    override fun getAllUsers(limit: Int, offset: Int): List<User> {
        return userRepository
            .findAll(OffsetBasedPageRequest(limit, offset))
            .map(userMapper::mapToDTO)
            .toList()
    }
}
