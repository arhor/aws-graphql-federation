import { ApolloServer } from '@apollo/server';
import { startStandaloneServer } from '@apollo/server/standalone';
import { ApolloGateway, IntrospectAndCompose, RemoteGraphQLDataSource } from '@apollo/gateway';
import { v4 as uuid } from 'uuid';

const TOKEN_HEADER_PREFIX_NAME = 'Bearer ';
const TOKEN_HEADER_PREFIX_SIZE = TOKEN_HEADER_PREFIX_NAME.length;

const gateway = new ApolloGateway({
    supergraphSdl: new IntrospectAndCompose({
        subgraphs: [
            { url: 'http://localhost:5001/graphql', name: 'users' },
            { url: 'http://localhost:5002/graphql', name: 'posts' },
            { url: 'http://localhost:5003/graphql', name: 'comments' },
        ],
    }),
    buildService: ({ url }) => new RemoteGraphQLDataSource({
        url,
        willSendRequest: ({ request, context }) => {
            request.http.headers.set('x-request-id', uuid());
        }
    })
});

const server = new ApolloServer({
    gateway,
});

const { url } = await startStandaloneServer(server);

console.log(`🚀 Server listening at: ${url}`);
