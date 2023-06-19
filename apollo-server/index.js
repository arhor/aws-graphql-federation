import { ApolloServer } from '@apollo/server';
import { startStandaloneServer } from '@apollo/server/standalone';
import { ApolloGateway, IntrospectAndCompose, RemoteGraphQLDataSource } from '@apollo/gateway';

const server = new ApolloServer({
    gateway: new ApolloGateway({
        supergraphSdl: new IntrospectAndCompose({
            subgraphs: [
                { url: 'http://localhost:5001/graphql', name: 'users' },
                { url: 'http://localhost:5002/graphql', name: 'articles' },
                { url: 'http://localhost:5003/graphql', name: 'comments' },
                { url: 'http://localhost:5004/graphql', name: 'extradata' },
            ],
        }),
        buildService: ({ url }) => new RemoteGraphQLDataSource({
            url,
            willSendRequest: ({ request, context }) => {
                if (context.user) {
                    request.http.headers.set('user', JSON.stringify(context.user));
                }
            }
        })
    }),
});

const { url } = await startStandaloneServer(server, {
    context: async ({ req }) => {
        const token = req.headers.authorization || '';
        const user = await getUser(token);

        return { user };
    },
});

console.log(`🚀 Server listening at: ${url}`);
