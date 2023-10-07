import * as uuid from 'uuid';
import fastify from 'fastify';
import fastifyJwt from '@fastify/jwt';
import fastifyApollo, { fastifyApolloDrainPlugin } from '@as-integrations/fastify';
import { ApolloServer } from '@apollo/server';
import { createGateway } from '#server/gateway.js';
import { gatewayPort } from '#server/utils/env.js';

const server = fastify({ logger: true });
const apollo = new ApolloServer({
    gateway: createGateway(server),
    plugins: [fastifyApolloDrainPlugin(server)],
});

await apollo.start();

await server.register(fastifyJwt, {
    secret: 'supersecret',
});
await server.register(fastifyApollo(apollo), {
    context: (req) => ({
        currentUser: req.user?.payload,
        requestUuid: uuid.v4(),
    }),
});

server.addHook('onRequest', async (req) => {
    try {
        await req.jwtVerify();
    } catch (err) {
        /* nothing to do with it */
    }
});

await server.listen({ port: gatewayPort });
