package com.github.arhor.aws.graphql.federation.starter.security

import com.github.arhor.aws.graphql.federation.common.toSet
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.UUID

/**
 * Throws an AccessDeniedException if the acting user has not the necessary
 * authorities and does not have the target user id.
 *
 * @param targetUserId The UUID of the target user related to the action.
 * @param actingUser   The user details of the acting user attempting to perform
 *                     the action.
 * @param authorities  A variable number of authority strings that the acting
 *                     user must have to execute the action.

 * @throws AccessDeniedException If the acting user does not have the required
 *                               authorities and does not have the target user ui.
 *
 * Example usage:
 * ```
 * ensureAccessAllowed(targetUserId, actingUser, "ROLE_ADMIN")
 * // Action to be performed if access is granted
 * performSensitiveOperation()
 * ```
 */
fun ensureAccessAllowed(
    targetUserId: UUID?,
    actingUser: CurrentUserDetails,
    vararg authorities: GrantedAuthority,
) {
    if (
        actingUser hasId targetUserId ||
        actingUser hasAuthorities authorities
    ) {
        return
    }
    throw AccessDeniedException("Access Denied")
}

val UserDetails.isUser: Boolean
    get() = PredefinedAuthority.ROLE_USER.name in authorities.toSet { it.authority }

val UserDetails.isAdmin: Boolean
    get() = PredefinedAuthority.ROLE_ADMIN.name in authorities.toSet { it.authority }

private infix fun CurrentUserDetails.hasId(targetUserId: UUID?): Boolean {
    return id == targetUserId
}

private infix fun CurrentUserDetails.hasAuthorities(requiredAuthorities: Array<out GrantedAuthority>): Boolean {
    return requiredAuthorities.isNotEmpty()
        && authorities.containsAll(requiredAuthorities.asList())
}
