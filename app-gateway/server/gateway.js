import gql from 'graphql-tag';
import fetcher from 'make-fetch-happen';
import { ApolloGateway, IntrospectAndCompose, LocalGraphQLDataSource, RemoteGraphQLDataSource } from '@apollo/gateway';
import { buildSubgraphSchema } from '@apollo/subgraph';
import { commsServiceUrl, postsServiceUrl, usersServiceUrl } from '#server/utils/env.js';
import { headers } from '#server/utils/constants.js';

export const gateway = new ApolloGateway({
    supergraphSdl: new IntrospectAndCompose({
        subgraphs: [
            { name: 'auth', url: 'auth' },
            { url: `${usersServiceUrl}/graphql`, name: 'users' },
            { url: `${postsServiceUrl}/graphql`, name: 'posts' },
            { url: `${commsServiceUrl}/graphql`, name: 'comments' },
        ],
    }),
    buildService({ url, name }) {
        return name === 'auth'
            ? createLocalDataSource()
            : createRemoteDatasource({ url, name });
    },
});

function createLocalDataSource() {
    return new LocalGraphQLDataSource(
        buildSubgraphSchema({
            typeDefs: gql`
                type Mutation {
                    signIn(input: SignInInput!): SignInResult
                    test(username: String, password: String): String
                }

                input SignInInput {
                    username: String!
                    password: String!
                }

                type SignInResult {
                    accessToken: String
                }
            `,
            resolvers: {
                Mutation: {
                    signIn: async (source, args, context, info) => {
                        const currentUser =
                            await fetch(`${usersServiceUrl}/verify-user`, {
                                method: 'POST',
                                headers: { 'Content-Type': 'application/json' },
                                body: JSON.stringify(args.input),
                            }).then(it => it.json());

                        return {
                            accessToken: `${currentUser}`,
                        };
                    },
                },
            },
        }),
    );
}

function createRemoteDatasource({ url, name }) {
    return new RemoteGraphQLDataSource({
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
    });
}
