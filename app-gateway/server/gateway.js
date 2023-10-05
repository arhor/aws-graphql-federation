import fetcher from 'make-fetch-happen';
import { ApolloGateway, IntrospectAndCompose, RemoteGraphQLDataSource } from '@apollo/gateway';
import { commsServiceUrl, postsServiceUrl, usersServiceUrl } from '#server/utils/env.js';
import { headers } from '#server/utils/constants.js';

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
        willSendRequest({ request, context, kind, incomingRequestContext }) {
            const {
                requestUuid,
                currentUser,
            } = context;

            if (requestUuid) {
                request.http.headers.set(headers.X_REQUEST_ID, requestUuid);
            }
            if (currentUser) {
                request.http.headers.set(headers.X_CURRENT_USER, JSON.stringify(currentUser));
            }
        },
    }),
});
