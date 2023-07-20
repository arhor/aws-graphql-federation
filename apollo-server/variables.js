export function required(variable) {
    return process.env[variable] || (() => {
        throw Error(`Missing env variable: ${variable}`);
    })();
}

export const usersServiceUrl = required('SUBGRAPH_URL_USERS');
export const postsServiceUrl = required('SUBGRAPH_URL_POSTS');
export const commsServiceUrl = required('SUBGRAPH_URL_COMMS');
