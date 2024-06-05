package com.github.arhor.aws.graphql.federation.users.service

import com.github.arhor.aws.graphql.federation.starter.security.CurrentUser
import com.github.arhor.aws.graphql.federation.starter.security.CurrentUserRequest
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.CreateUserInput
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.DeleteUserInput
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.UpdateUserInput
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.User
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.UserPage
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.UsersLookupInput
import java.util.UUID

/**
 * UserService interface that defines operations related to user management.
 */
interface UserService {

    /**
     * Retrieves a user by their unique identifier.
     *
     * @param id the unique identifier of the user
     * @return the user associated with the specified ID
     */
    fun getUserById(id: UUID): User

    /**
     * Retrieves a paginated list of users based on the provided lookup criteria.
     *
     * @param input the criteria for looking up users
     * @return a paginated list of users
     */
    fun getUserPage(input: UsersLookupInput): UserPage

    /**
     * Authenticates a user based on their username and password.
     *
     * @param request the request containing the username and password
     * @return the authenticated user
     */
    fun getUserByUsernameAndPassword(request: CurrentUserRequest): CurrentUser

    /**
     * Creates a new user based on the provided input.
     *
     * @param input the input containing details for creating a new user
     * @return the created user
     */
    fun createUser(input: CreateUserInput): User

    /**
     * Updates an existing user based on the provided input.
     *
     * @param input the input containing details for updating an existing user
     * @return the updated user
     */
    fun updateUser(input: UpdateUserInput): User

    /**
     * Deletes an existing user based on the provided input.
     *
     * @param input the input containing details for deleting an existing user
     * @return `true` if the user was successfully deleted, `false` otherwise
     */
    fun deleteUser(input: DeleteUserInput): Boolean
}
