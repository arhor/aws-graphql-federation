package com.github.arhor.aws.graphql.federation.users.service

import com.github.arhor.aws.graphql.federation.security.CurrentUser
import com.github.arhor.aws.graphql.federation.security.CurrentUserRequest
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.CreateUserInput
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.CreateUserResult
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.DeleteUserInput
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.DeleteUserResult
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.UpdateUserInput
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.UpdateUserResult
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.User
import com.github.arhor.aws.graphql.federation.users.generated.graphql.types.UsersLookupInput

interface UserService {
    fun getUserById(id: Long): User
    fun getAllUsers(input: UsersLookupInput): List<User>
    fun verifyUser(request: CurrentUserRequest): CurrentUser
    fun createUser(input: CreateUserInput): CreateUserResult
    fun updateUser(input: UpdateUserInput): UpdateUserResult
    fun deleteUser(input: DeleteUserInput): DeleteUserResult
}
