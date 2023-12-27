package com.github.arhor.aws.graphql.federation.users.service.impl

import com.github.arhor.aws.graphql.federation.common.event.UserEvent
import com.github.arhor.aws.graphql.federation.common.exception.EntityDuplicateException
import com.github.arhor.aws.graphql.federation.common.exception.EntityNotFoundException
import com.github.arhor.aws.graphql.federation.common.exception.Operation
import com.github.arhor.aws.graphql.federation.security.CurrentUser
import com.github.arhor.aws.graphql.federation.security.CurrentUserRequest
import com.github.arhor.aws.graphql.federation.tracing.Trace
import com.github.arhor.aws.graphql.federation.users.data.repository.AuthRepository
import com.github.arhor.aws.graphql.federation.users.data.repository.UserRepository
import com.github.arhor.aws.graphql.federation.users.generated.graphql.DgsConstants.USER
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.CreateUserInput
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.CreateUserResult
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.DeleteUserInput
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.DeleteUserResult
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.UpdateUserInput
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.UpdateUserResult
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.User
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.UsersLookupInput
import com.github.arhor.aws.graphql.federation.users.service.UserService
import com.github.arhor.aws.graphql.federation.users.service.events.UserEventEmitter
import com.github.arhor.aws.graphql.federation.users.service.mapping.UserMapper
import com.netflix.graphql.dgs.exceptions.DgsBadRequestException
import org.slf4j.LoggerFactory
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.retry.annotation.Retryable
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Trace
@Service
class UserServiceImpl(
    private val userMapper: UserMapper,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val userEventEmitter: UserEventEmitter,
    private val passwordEncoder: PasswordEncoder,
) : UserService {

    @Transactional(readOnly = true)
    override fun getUserById(id: Long): User {
        return userRepository.findByIdOrNull(id)?.let { userMapper.mapToResult(it) }
            ?: throw EntityNotFoundException(
                entity = USER.TYPE_NAME,
                condition = "${USER.Id} = $id",
                operation = Operation.READ,
            )
    }

    @Transactional(readOnly = true)
    override fun getAllUsers(input: UsersLookupInput): List<User> {
        return userRepository
            .findAll(PageRequest.of(input.page, input.size))
            .map(userMapper::mapToResult)
            .toList()
    }

    @Transactional(readOnly = true)
    override fun verifyUser(request: CurrentUserRequest): CurrentUser {
        val (username, password) = request
        val user = userRepository.findByUsername(username)

        if (user != null) {
            if (passwordEncoder.matches(password, user.password)) {
                authRepository.findAllById(user.authorities.map { it.authId.id })
                return CurrentUser(
                    id = user.id!!,
                    authorities = listOf(ROLE_USER)
                )
            } else {
                logger.error("Provided incorrect password for the user with id: {}", user.id)
            }
        } else {
            logger.error("Provided incorrect username: {}", username)
        }
        throw DgsBadRequestException(message = "Bad Credentials")
    }

    @Transactional
    override fun createUser(input: CreateUserInput): CreateUserResult {
        if (userRepository.existsByUsername(input.username)) {
            throw EntityDuplicateException(
                entity = USER.TYPE_NAME,
                condition = "${USER.Username} = ${input.username}",
                operation = Operation.CREATE,
            )
        }
        val user = input.copy(password = passwordEncoder.encode(input.password))
            .let { userMapper.mapToEntity(it) }
            .let { userRepository.save(it) }
            .let { userMapper.mapToResult(it) }

        return CreateUserResult(user)
    }

    @Transactional
    @Retryable(retryFor = [OptimisticLockingFailureException::class])
    override fun updateUser(input: UpdateUserInput): UpdateUserResult {
        val initialState = userRepository.findByIdOrNull(input.id) ?: throw EntityNotFoundException(
            entity = USER.TYPE_NAME,
            condition = "${USER.Id} = ${input.id}",
            operation = Operation.UPDATE,
        )
        val currentState = initialState.copy(
            password = input.password?.let(passwordEncoder::encode) ?: initialState.password
        )
        val user = userMapper.mapToResult(
            entity = when (currentState != initialState) {
                true -> userRepository.save(currentState)
                else -> initialState
            }
        )
        return UpdateUserResult(user)
    }

    @Transactional
    override fun deleteUser(input: DeleteUserInput): DeleteUserResult {
        return DeleteUserResult(
            success = when (val user = userRepository.findByIdOrNull(input.id)) {
                null -> false
                else -> {
                    userRepository.delete(user)
                    userEventEmitter.emit(UserEvent.Deleted(id = user.id!!))
                    true
                }
            }
        )
    }

    companion object {
        const val ROLE_USER = "ROLE_USER"

        private val logger = LoggerFactory.getLogger(UserServiceImpl::class.java)
    }
}
