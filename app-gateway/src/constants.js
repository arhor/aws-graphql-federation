export const GRAPHQL_END_POINT = '/graphql';

export const USERS_SERVICE_BASE_PATH = String(required('SUBGRAPH_URL_USERS'));
export const POSTS_SERVICE_BASE_PATH = String(required('SUBGRAPH_URL_POSTS'));
export const COMMS_SERVICE_BASE_PATH = String(required('SUBGRAPH_URL_COMMS'));

export const USERS_SERVICE_VERIFY_PATH = `${USERS_SERVICE_BASE_PATH}/api/users/authenticate`;

export const USERS_SUBGRAPH_URL = `${USERS_SERVICE_BASE_PATH}${GRAPHQL_END_POINT}`;
export const POSTS_SUBGRAPH_URL = `${POSTS_SERVICE_BASE_PATH}${GRAPHQL_END_POINT}`;
export const COMMS_SUBGRAPH_URL = `${COMMS_SERVICE_BASE_PATH}${GRAPHQL_END_POINT}`;

export const SUBGRAPH = Object.freeze({
    USERS: USERS_SUBGRAPH_URL,
    POSTS: POSTS_SUBGRAPH_URL,
    COMMS: COMMS_SUBGRAPH_URL,
    LOCAL: 'local'
});

export const GATEWAY_PORT = Number(process.env['GATEWAY_PORT'] ?? 4000);

export const ACCESS_TOKEN = Object.freeze({
    COOKIE: 'ACCESS_TOKEN',
    EXPIRE: 60 * 15,
});

export function required(variable) {
    return process.env[variable] || (() => {
        throw Error(`Missing env variable: ${variable}`);
    })();
}
