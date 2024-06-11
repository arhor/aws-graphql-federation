import { ApolloGateway, IntrospectAndCompose, LocalGraphQLDataSource, RemoteGraphQLDataSource } from '@apollo/gateway';
import { ApolloServer } from '@apollo/server';
import { buildSubgraphSchema } from '@apollo/subgraph';
import fastifyApollo, { fastifyApolloDrainPlugin } from '@as-integrations/fastify';
import gql from 'graphql-tag';
import fetcher from 'make-fetch-happen';

import { ACCESS_TOKEN_COOKIE, COMMS_SUBGRAPH_URL, POSTS_SUBGRAPH_URL, USERS_SUBGRAPH_URL } from '#src/constants.js';
import { authenticate } from "#src/user-service-client.js";

export async function createApollo() {
    return async (server) => {
        const apollo = new ApolloServer({
            gateway: createGateway(),
            plugins: [fastifyApolloDrainPlugin(server)],
        });

        await apollo.start();

        server.register(fastifyApollo(apollo), {
            context: (req, res) => ({
                req,
                res,
                tracingUuid: req.id,
                currentUser: req.user?.payload,
            }),
        });
    };
}

function createGateway() {
    return new ApolloGateway({
        supergraphSdl: new IntrospectAndCompose({
            subgraphs: [
                { name: 'auth', url: 'auth' },
                { url: USERS_SUBGRAPH_URL, name: 'users' },
                { url: POSTS_SUBGRAPH_URL, name: 'posts' },
                { url: COMMS_SUBGRAPH_URL, name: 'comments' },
            ],
        }),
        buildService({ url, name }) {
            switch (name) {
                case 'auth':
                    return createLocalDataSource();
                default:
                    return createRemoteDatasource({ url, name });
            }
        },
    });
}

function createLocalDataSource() {
    return new LocalGraphQLDataSource(
        buildSubgraphSchema({
            typeDefs: gql`
                type Query {
                    me: CurrentUser
                }

                type Mutation {
                    signIn(input: SignInInput!): Boolean
                }

                input SignInInput {
                    username: String!
                    password: String!
                }

                type CurrentUser {
                    id: ID
                    authorities: [String!]
                    authenticated: Boolean!
                }
            `,
            resolvers: {
                Query: {
                    me: (source, args, context) => {
                        const { currentUser } = context;

                        return currentUser ? {
                            id: currentUser.id,
                            authorities: currentUser.authorities,
                            authenticated: true,
                        } : {
                            authenticated: false,
                        };
                    },
                },
                Mutation: {
                    signIn: async (source, args, context) => {
                        const principal = await authenticate(args.input);
                        const signedJwt = await context.res.jwtSign({ payload: principal });

                        context.res.setCookie(ACCESS_TOKEN_COOKIE, signedJwt, {
                            path: '/',
                            maxAge: 1000 * 60 * 60 * 24,
                            secure: false,
                            httpOnly: true,
                        });

                        return true;
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
