import * as uuid from 'uuid';
import fastify from 'fastify';
import fastifyApollo, { fastifyApolloDrainPlugin } from '@as-integrations/fastify';
import fastifyCookie from '@fastify/cookie';
import fastifyJwt from '@fastify/jwt';
import { ApolloServer } from '@apollo/server';

import { createGateway } from '#server/gateway.js';
import { ACCESS_TOKEN_COOKIE, GATEWAY_PORT } from '#server/utils/constants.js';

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
    cookie: {
        cookieName: ACCESS_TOKEN_COOKIE,
        signed: false,
    },
});

await server.register(fastifyCookie);

await server.register(fastifyApollo(apollo), {
    context: (req, res) => ({
        req,
        res,
        tracingUuid: req.id,
        currentUser: req.user?.payload,
    }),
});

server.addHook('onRequest', async (req) => {
    if (req.cookies[ACCESS_TOKEN_COOKIE]) {
        await req.jwtVerify();
    }
});

await server.listen({ port: GATEWAY_PORT, host: '0.0.0.0' });
