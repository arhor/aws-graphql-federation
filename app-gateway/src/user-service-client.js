import { GraphQLError } from 'graphql';

import { USERS_SERVICE_VERIFY_PATH } from '#src/constants.js';

/**
 * Authenticates a user by sending their username and password to the Users service for verification.
 *
 * @param {Object} credentials - The user's credentials.
 * @param {string} credentials.username - The username of the user to authenticate.
 * @param {string} credentials.password - The password of the user to authenticate.
 * @returns {Promise<any>} An authenticated user details if the authentication is successful.
 */
export async function authenticate({ username, password }) {
    let success;
    let failure;

    try {
        const response = await fetch(USERS_SERVICE_VERIFY_PATH, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password }),
        });
        const content = await response.json();

        if (response.ok) {
            success = content;
        } else {
            failure = {
                message: content.message ?? 'Bad Credentials',
                code: 'AUTHENTICATION_ERROR',
            };
        }
    } catch (error) {
        failure = {
            message: error.message ?? 'Internal server error',
            code: 'INTERNAL_SERVER_ERROR'
        };
    }

    if (failure) {
        throw new GraphQLError(failure.message, {
            extensions: {
                code: failure.code,
            },
        });
    }
    return success;
}
