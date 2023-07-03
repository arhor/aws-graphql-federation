import { ApolloServer } from '@apollo/server';
import { startStandaloneServer } from '@apollo/server/standalone';
import { ApolloGateway, IntrospectAndCompose, RemoteGraphQLDataSource } from '@apollo/gateway';
import { v4 as uuid } from 'uuid';

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
            request.http.headers.set('x-request-id', context.globalRequestId);
        },
    }),
});

const server = new ApolloServer({
    gateway,
});

const { url } = await startStandaloneServer(server, {
    context: () => {
        return {
            globalRequestId: uuid(),
        };
    },
});

console.log(`🚀 Server listening at: ${url}`);
