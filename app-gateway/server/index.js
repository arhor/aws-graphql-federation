import * as uuid from 'uuid';
import fastify from 'fastify';
import fastifyJwt from '@fastify/jwt';
import fastifyApollo, { fastifyApolloDrainPlugin } from '@as-integrations/fastify';
import { ApolloServer } from '@apollo/server';
import { createGateway } from '#server/gateway.js';
import { gatewayPort } from '#server/utils/env.js';

const server = fastify({
    logger: process.env.NODE_ENV === 'production' ? {
        level: 'info',
        redact: [
            'req.headers.authorization',
        ],
        transport: {
            options: {
                ignore: 'pid,hostname',
            },
        },
    } : process.env.NODE_ENV === 'development' ? {
        level: 'debug',
        transport: {
            target: 'pino-pretty',
            options: {
                ignore: 'pid,hostname',
            }
        }
    } : false,
    requestIdHeader: 'x-tracing-id',
    requestIdLogLabel: 'tracing-id',
    genReqId: () => uuid.v4(),
});
const apollo = new ApolloServer({
    gateway: createGateway(server),
    plugins: [fastifyApolloDrainPlugin(server)],
});

await apollo.start();

await server.register(fastifyJwt, {
    secret: uuid.v4(),
});
await server.register(fastifyApollo(apollo), {
    context: (req) => ({
        tracingUuid: req.id,
        currentUser: req.user?.payload,
    }),
});

server.addHook('onRequest', async (req) => {
    if (req.headers.authorization) {
        await req.jwtVerify();
    }
});

await server.listen({ port: gatewayPort });
