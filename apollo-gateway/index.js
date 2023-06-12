import { ApolloServer } from '@apollo/server';
import { startStandaloneServer } from '@apollo/server/standalone';
import { ApolloGateway, IntrospectAndCompose } from '@apollo/gateway';

const { url } = await startStandaloneServer(
    new ApolloServer({
        gateway: new ApolloGateway({
            supergraphSdl: new IntrospectAndCompose({
                subgraphs: [
                    {
                        name: 'users',
                        url: 'http://localhost:5001/graphql',
                    },
                    {
                        name: 'topics',
                        url: 'http://localhost:5002/graphql',
                    },
                    {
                        name: 'extraData',
                        url: 'http://localhost:5003/graphql',
                    },
                ],
            }),
        }),
    })
);

console.log(`🚀  Server ready at ${url}`);
