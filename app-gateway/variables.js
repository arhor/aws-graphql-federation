export function required(variable) {
    return process.env[variable] || (() => {
        throw Error(`Missing env variable: ${variable}`);
    })();
}

export const usersServiceUrl = String(required('SUBGRAPH_URL_USERS'));
export const postsServiceUrl = String(required('SUBGRAPH_URL_POSTS'));
export const commsServiceUrl = String(required('SUBGRAPH_URL_COMMS'));
export const gatewayPort = Number(process.env['GATEWAY_PORT'] ?? 4000);
