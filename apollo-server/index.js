import { ApolloServer } from '@apollo/server';
import { startStandaloneServer } from '@apollo/server/standalone';
import { ApolloGateway, IntrospectAndCompose, RemoteGraphQLDataSource } from '@apollo/gateway';
import { verify } from 'jsonwebtoken';

const gateway = new ApolloGateway({
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
            const currentUser = context.user;
            if (currentUser) {
                request.http.headers.set('x-current-user', JSON.stringify(currentUser));
            }
        }
    })
});

const server = new ApolloServer({
    gateway,
});

const { url } = await startStandaloneServer(server, {
    context: async ({ req }) => {
        const header = req.headers.authorization;
        if (header) {
            const token = header.replace('Bearer ', '');
            if (token) {
                const data = await fetch('http://localhost:5001/api/jwt/verify', { method: 'POST', body: JSON.stringify({ token }) });
                const user = await data.json();

                return {
                    user: {
                        id: user.id,
                    }
                };
            }
        }
        return {};
    },
});

console.log(`🚀 Server listening at: ${url}`);
