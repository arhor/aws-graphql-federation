import { ApolloGateway, IntrospectAndCompose, RemoteGraphQLDataSource } from '@apollo/gateway';
import fetcher from 'make-fetch-happen';
import { commsServiceUrl, postsServiceUrl, usersServiceUrl } from './variables.js';

export const gateway = new ApolloGateway({
    supergraphSdl: new IntrospectAndCompose({
        subgraphs: [
            { url: `${usersServiceUrl}/graphql`, name: 'users' },
            { url: `${postsServiceUrl}/graphql`, name: 'posts' },
            { url: `${commsServiceUrl}/graphql`, name: 'comments' },
        ],
    }),
    buildService: ({ url, name }) => new RemoteGraphQLDataSource({
        url,
        name,
        fetcher: fetcher.defaults({
            retry: {
                retries: 5,
                factor: 2,
                minTimeout: 1000,
                maxTimeout: 60 * 1000,
                randomize: true,
            },
            onRetry(cause) {
                console.log('Retrying...', cause);
            }
        }),
        willSendRequest({ request, context }) {
            const {
                requestUuid,
                currentUser,
            } = context;

            if (requestUuid) {
                request.http.headers.set('X-Request-ID', requestUuid);
            }
            if (currentUser) {
                request.http.headers.set('X-Current-User', JSON.stringify(currentUser));
            }
        },
    }),
});
