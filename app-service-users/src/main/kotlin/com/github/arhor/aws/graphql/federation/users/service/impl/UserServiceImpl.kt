package com.github.arhor.aws.graphql.federation.users.service.impl

import com.github.arhor.aws.graphql.federation.common.event.UserEvent
import com.github.arhor.aws.graphql.federation.common.exception.EntityDuplicateException
import com.github.arhor.aws.graphql.federation.common.exception.EntityNotFoundException
import com.github.arhor.aws.graphql.federation.common.exception.EntityOperationRestrictedException
import com.github.arhor.aws.graphql.federation.common.exception.Operation
import com.github.arhor.aws.graphql.federation.starter.security.CurrentUser
import com.github.arhor.aws.graphql.federation.starter.security.CurrentUserRequest
import com.github.arhor.aws.graphql.federation.starter.security.PredefinedAuthority
import com.github.arhor.aws.graphql.federation.starter.tracing.Trace
import com.github.arhor.aws.graphql.federation.users.data.model.UserEntity
import com.github.arhor.aws.graphql.federation.users.data.repository.AuthRepository
import com.github.arhor.aws.graphql.federation.users.data.repository.UserRepository
import com.github.arhor.aws.graphql.federation.users.generated.graphql.DgsConstants.USER
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.CreateUserInput
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.UpdateUserInput
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.User
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.UserPage
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.UsersLookupInput
import com.github.arhor.aws.graphql.federation.users.service.UserService
import com.github.arhor.aws.graphql.federation.users.service.mapping.UserMapper
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Trace
@Service
class UserServiceImpl(
    private val userMapper: UserMapper,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val eventPublisher: ApplicationEventPublisher,
    private val passwordEncoder: PasswordEncoder,
) : UserService {

    @Transactional(readOnly = true)
    override fun getUserById(id: UUID): User {
        return userRepository.findByIdOrNull(id)?.let { userMapper.mapToResult(it) }
            ?: throw EntityNotFoundException(
                entity = USER.TYPE_NAME,
                condition = "${USER.Id} = $id",
                operation = Operation.LOOKUP,
            )
    }

    @Transactional(readOnly = true)
    override fun getUserPage(input: UsersLookupInput): UserPage {
        return userRepository
            .findAll(PageRequest.of(input.page, input.size))
            .map(userMapper::mapToResult)
            .let {
                UserPage(
                    data = it.content,
                    page = input.page,
                    size = input.size,
                    hasPrev = it.hasPrevious(),
                    hasNext = it.hasNext(),
                )
            }
    }

    @Transactional(readOnly = true)
    override fun getUserByUsernameAndPassword(request: CurrentUserRequest): CurrentUser {
        val (username, password) = request
        val user = userRepository.findByUsername(username)

        if (user != null) {
            if (passwordEncoder.matches(password, user.password)) {
                val authorities = user.authorities
                    .map { it.authId }
                    .let { authRepository.findAllById(it) }

                return userMapper.mapToCurrentUser(user, authorities)
            } else {
                logger.error("Provided incorrect password for the user with id: {}", user.id)
            }
        } else {
            logger.error("Provided incorrect username: {}", username)
        }
        throw UsernameNotFoundException("Bad Credentials")
    }

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
            .let { userMapper.mapToEntity(it, auth = authRepository.findByName(PredefinedAuthority.ROLE_USER)) }
            .let { userRepository.save(it) }
            .also { eventPublisher.publishEvent(UserEvent.Created(id = it.id!!)) }
            .let { userMapper.mapToResult(it) }
    }

    @Transactional
    override fun updateUser(input: UpdateUserInput): User {
        val initialState = userRepository.findByIdOrNull(input.id) ?: throw EntityNotFoundException(
            entity = USER.TYPE_NAME,
            condition = "${USER.Id} = ${input.id}",
            operation = Operation.UPDATE,
        )
        val currentState = initialState.copy(
            password = input.password?.let(passwordEncoder::encode) ?: initialState.password
        )
        return userMapper.mapToResult(
            entity = when (currentState != initialState) {
                true -> trySaveHandlingConcurrentUpdates(currentState)
                else -> initialState
            }
        )
    }

    @Transactional
    override fun deleteUser(id: UUID): Boolean {
        return when (val user = userRepository.findByIdOrNull(id)) {
            null -> false
            else -> {
                userRepository.delete(user)
                eventPublisher.publishEvent(UserEvent.Deleted(id = user.id!!))
                true
            }
        }
    }

    private fun trySaveHandlingConcurrentUpdates(entity: UserEntity): UserEntity {
        return try {
            userRepository.save(entity)
        } catch (e: OptimisticLockingFailureException) {
            logger.error(e.message, e)

            throw EntityOperationRestrictedException(
                entity = USER.TYPE_NAME,
                condition = "${USER.Id} = ${entity.id} (updated concurrently)",
                operation = Operation.UPDATE,
                cause = e,
            )
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.enclosingClass)
    }
}
