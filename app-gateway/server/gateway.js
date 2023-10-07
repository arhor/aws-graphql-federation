import gql from 'graphql-tag';
import fetcher from 'make-fetch-happen';
import { ApolloGateway, IntrospectAndCompose, LocalGraphQLDataSource, RemoteGraphQLDataSource } from '@apollo/gateway';
import { buildSubgraphSchema } from '@apollo/subgraph';
import { commsServiceUrl, postsServiceUrl, usersServiceUrl } from '#server/utils/env.js';

export function createGateway(server) {
    return new ApolloGateway({
        supergraphSdl: new IntrospectAndCompose({
            subgraphs: [
                { name: 'auth', url: 'auth' },
                { url: `${usersServiceUrl}/graphql`, name: 'users' },
                { url: `${postsServiceUrl}/graphql`, name: 'posts' },
                { url: `${commsServiceUrl}/graphql`, name: 'comms' },
            ],
        }),
        buildService({ url, name }) {
            return name === 'auth'
                ? createLocalDataSource({ server })
                : createRemoteDatasource({ url, name });
        },
    });
}

function createLocalDataSource({ server }) {
    return new LocalGraphQLDataSource(
        buildSubgraphSchema({
            typeDefs: gql`
                type Mutation {
                    signIn(input: SignInInput!): SignInResult
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
                    signIn: async (source, args) => {
                        const accessToken =
                            await fetch(`${usersServiceUrl}/api/users/verify`, {
                                method: 'POST',
                                headers: { 'Content-Type': 'application/json' },
                                body: JSON.stringify(args.input),
                            })
                                .then(it => it.json())
                                .then(it => server.jwt.sign({ payload: it }));

                        return { accessToken };
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
    });
}
