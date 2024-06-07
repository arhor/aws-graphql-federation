import gql from 'graphql-tag';
import fetcher from 'make-fetch-happen';
import { ApolloGateway, IntrospectAndCompose, LocalGraphQLDataSource, RemoteGraphQLDataSource } from '@apollo/gateway';
import { buildSubgraphSchema } from '@apollo/subgraph';
import { COMMS_SERVICE_GRAPHQL_URL, POSTS_SERVICE_GRAPHQL_URL, USERS_SERVICE_GRAPHQL_URL } from '#server/utils/env.js';

export function createGateway(server) {
    return new ApolloGateway({
        supergraphSdl: new IntrospectAndCompose({
            subgraphs: [
                { name: 'auth', url: 'auth' },
                { url: USERS_SERVICE_GRAPHQL_URL, name: 'users' },
                { url: POSTS_SERVICE_GRAPHQL_URL, name: 'posts' },
                { url: COMMS_SERVICE_GRAPHQL_URL, name: 'comments' },
            ],
        }),
        buildService({ url, name }) {
            switch (name) {
                case 'auth':
                    return createLocalDataSource({ server });
                default:
                    return createRemoteDatasource({ url, name });
            }
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
                        const principal = await vauthenticate(args.input);
                        const signedJwt = server.jwt.sign({ payload: principal });

                        return { accessToken: signedJwt };
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
                tracingUuid,
                currentUser,
            } = context;

            if (tracingUuid) {
                request.http.headers.set('x-tracing-id', tracingUuid);
            }
            if (currentUser) {
                request.http.headers.set('x-current-user', JSON.stringify(currentUser));
            }
        },
    });
}
