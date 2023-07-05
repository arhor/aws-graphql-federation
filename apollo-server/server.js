import { ApolloServer } from '@apollo/server';
import { startStandaloneServer } from '@apollo/server/standalone';
import { ApolloGateway, IntrospectAndCompose, RemoteGraphQLDataSource } from '@apollo/gateway';
import { v4 as uuid } from 'uuid';

function required(variable) {
    return process.env[variable] || (() => { throw Error(`Missing env variable: ${variable}`) })();
}

const gateway = new ApolloGateway({
    /**
     * This property defines Supergraph Schema, composing it from subgraphs in downstream services. 
     */
    supergraphSdl: new IntrospectAndCompose({
        subgraphs: [
            { url: required('SUBGRAPH_URL_USERS'), name: 'users' },
            { url: required('SUBGRAPH_URL_POSTS'), name: 'posts' },
            { url: required('SUBGRAPH_URL_COMMS'), name: 'comments' },
        ],
    }),
    buildService: ({ url }) => new RemoteGraphQLDataSource({
        url,

        /**
         * This function will be invoked before every request to downstream services.
         */
        willSendRequest: ({ request, context }) => {
            request.http.headers.set('x-request-id', context.globalRequestId ?? uuid());
        },
    }),
});

const server = new ApolloServer({
    gateway,
});

const { url } = await startStandaloneServer(server, {
    /**
     * This function allows to define execution context of incoming request.
     * It will be invoked once before processing.
     */
    context: () => {
        return {
            globalRequestId: uuid(),
        };
    },
});

console.log(`🚀 Server listening at: ${url}`);
