package com.github.arhor.aws.graphql.federation.security

import com.github.arhor.aws.graphql.federation.common.toSet
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.userdetails.UserDetails
import java.util.UUID

/**
 * Executes a given action if the acting user has the necessary authorities or is the target user.
 * Otherwise, an AccessDeniedException is thrown.
 *
 * @param T The return type of the action to be executed.
 * @param actingUser The user details of the acting user attempting to perform the action.
 * @param targetUserId The UUID of the target user related to the action.
 * @param authorities A variable number of authority strings that the acting user must have to execute the action.
 * @param action A lambda function representing the action to be executed if access is granted.
 * @return The result of the executed action.
 * @throws AccessDeniedException If the acting user does not have the required authorities and is not the target user.
 *
 * Example usage:
 * ```
 * val result = securedAccess(actingUser, targetUserId, "ROLE_ADMIN") {
 *     // Action to be performed if access is granted
 *     performSensitiveOperation()
 * }
 * ```
 */
fun <T : Any> securedAccess(
    actingUser: UserDetails,
    targetUserId: UUID,
    vararg authorities: String,
    action: () -> T,
): T {
    if (
        actingUser.hasAuthorities(authorities) ||
        actingUser.hasId(targetUserId)
    ) {
        return action()
    }
    throw AccessDeniedException("Access Denied")
}

private fun UserDetails.hasAuthorities(requiredAuthorities: Array<out String>): Boolean {
    return requiredAuthorities.isNotEmpty()
        && authorities.toSet { it.authority }.containsAll(requiredAuthorities.asList())
}

private fun UserDetails.hasId(targetUserId: UUID): Boolean {
    return targetUserId == UUID.fromString(username)
}
