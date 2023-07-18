import { ApolloGateway, IntrospectAndCompose, RemoteGraphQLDataSource } from '@apollo/gateway';
import fetcher from 'make-fetch-happen';
import * as uuid from 'uuid';

export function required(variable) {
    return process.env[variable] || (() => {
        throw Error(`Missing env variable: ${variable}`);
    })();
}

export const gateway = new ApolloGateway({
    supergraphSdl: new IntrospectAndCompose({
        subgraphs: [
            { url: required('SUBGRAPH_URL_USERS'), name: 'users' },
            { url: required('SUBGRAPH_URL_POSTS'), name: 'posts' },
            { url: required('SUBGRAPH_URL_COMMS'), name: 'comments' },
        ],
    }),
    buildService: ({ url, name }) => new RemoteGraphQLDataSource({
        url,
        name,
        fetcher: fetcher.defaults({
            retry: {
                retries: 5,
                factor: 2,
                minTimeout: 1 * 1000,
                maxTimeout: 60 * 1000,
                randomize: true,
            },
            onRetry: (cause) => {
                console.log('Retrying...', cause);
            }
        }),
        willSendRequest: ({ request, context }) => {
            request.http.headers.set('x-request-id', context.requestId ?? uuid.v4());
            request.http.headers.set('x-current-user', context.currentUser ? JSON.stringify(context.currentUser) : null);
        },
    }),
});
