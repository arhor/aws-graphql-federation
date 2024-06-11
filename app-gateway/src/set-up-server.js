import * as uuid from 'uuid';
import fastify from 'fastify';
import fastifyCookie from '@fastify/cookie';
import fastifyJwt from '@fastify/jwt';

import { ACCESS_TOKEN, GRAPHQL_END_POINT } from '#src/constants.js';

export async function createServer(callback) {
    const server = fastify({
        logger: createLogger(),
        requestIdHeader: 'x-tracing-id',
        requestIdLogLabel: 'tracing-id',
        genReqId: () => uuid.v4(),
    });

    server.register(fastifyJwt, {
        secret: uuid.v4(),
        cookie: {
            cookieName: ACCESS_TOKEN.COOKIE,
            signed: false,
        },
    });

    server.register(fastifyCookie, {
        hook: 'onRequest',
    });

    server.get('/', (req, res) => {
        res.redirect(GRAPHQL_END_POINT);
    });

    server.addHook('onRequest', async (req, res) => {
        if (req.cookies[ACCESS_TOKEN.COOKIE]) {
            try {
                await req.jwtVerify({ onlyCookie: true });
            } catch (err) {
                req.log.error('An invalid access token found - removing it from the cookies', err);
                res.clearCookie(ACCESS_TOKEN.COOKIE);
            }
        }
    });

    await callback(server);

    return server;
}

function createLogger() {
    switch (process.env.NODE_ENV) {
        case 'production':
            return {
                level: 'info',
                redact: [
                    'req.headers.authorization',
                ],
                transport: {
                    options: {
                        ignore: 'pid,hostname',
                    },
                },
            };
        case 'development':
            return {
                level: 'debug',
                transport: {
                    target: 'pino-pretty',
                    options: {
                        ignore: 'pid,hostname',
                    }
                }
            };
        default:
            return false;
    }
}
