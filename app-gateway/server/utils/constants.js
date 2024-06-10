export const GRAPHQL_END_POINT = '/graphql';

const USERS_SERVICE_BASE_PATH = String(required('SUBGRAPH_URL_USERS'));
const POSTS_SERVICE_BASE_PATH = String(required('SUBGRAPH_URL_POSTS'));
const COMMS_SERVICE_BASE_PATH = String(required('SUBGRAPH_URL_COMMS'));

export const USERS_SERVICE_VERIFY_PATH = `${USERS_SERVICE_BASE_PATH}/api/users/authenticate`;

export const USERS_SERVICE_GRAPHQL_URL = `${USERS_SERVICE_BASE_PATH}${GRAPHQL_END_POINT}`;
export const POSTS_SERVICE_GRAPHQL_URL = `${POSTS_SERVICE_BASE_PATH}${GRAPHQL_END_POINT}`;
export const COMMS_SERVICE_GRAPHQL_URL = `${COMMS_SERVICE_BASE_PATH}${GRAPHQL_END_POINT}`;
export const GATEWAY_PORT = Number(process.env['GATEWAY_PORT'] ?? 4000);

export const ACCESS_TOKEN_COOKIE = 'ACCESS_TOKEN';

export function required(variable) {
    return process.env[variable] || (() => {
        throw Error(`Missing env variable: ${variable}`);
    })();
}
