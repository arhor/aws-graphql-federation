package com.github.arhor.aws.graphql.federation.users.service

import com.github.arhor.aws.graphql.federation.security.CurrentUser
import com.github.arhor.aws.graphql.federation.security.CurrentUserRequest
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.CreateUserInput
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.DeleteUserInput
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.UpdateUserInput
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.User
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.UsersLookupInput
import java.util.UUID

interface UserService {
    fun getUserById(id: UUID): User
    fun getAllUsers(input: UsersLookupInput): List<User>
    fun getUserByUsernameAndPassword(request: CurrentUserRequest): CurrentUser
    fun createUser(input: CreateUserInput): User
    fun updateUser(input: UpdateUserInput): User
    fun deleteUser(input: DeleteUserInput): Boolean
}
