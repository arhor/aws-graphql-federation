import { GraphQLError } from 'graphql';
import { USERS_SERVICE_VERIFY_PATH } from '#server/utils/env.js';

/**
 * Authenticates a user by sending their username and password to the Users service for verification.
 *
 * @param {Object} credentials - The user's credentials.
 * @param {string} credentials.username - The username of the user to authenticate.
 * @param {string} credentials.password - The password of the user to authenticate.
 * @returns {Promise<any>} A promise that resolves to the authenticated user details if the authentication is successful.
 */
export async function authenticate({ username, password }) {
    const response = await fetch(USERS_SERVICE_VERIFY_PATH, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password }),
    });
    if (!response.ok) {
        const error = await response.json();
        throw new GraphQLError(error.message, {
            extensions: {
                code: 'BAD_USER_INPUT',
            },
        });
    }
    return await response.json();
}
