import { GraphQLError } from 'graphql';
import { USERS_SERVICE_VERIFY_PATH } from '#server/utils/env.js';

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
