import { ApolloServer } from '@apollo/server';
import { expressMiddleware } from '@apollo/server/express4';
import { ApolloServerPluginDrainHttpServer } from '@apollo/server/plugin/drainHttpServer';
import express from 'express';
import { expressjwt } from 'express-jwt';
import http from 'http';
import cors from 'cors';
import bodyParser from 'body-parser';
import * as uuid from 'uuid';
import { gateway } from '#server/gateway.js';
import crypto from 'crypto';
import { gatewayPort, usersServiceUrl } from "#server/utils/env.js";

const { json } = bodyParser;

const publicKey =
    await fetch(`${usersServiceUrl}/public-key`)
        .then(it => it.text())
        .then(it => crypto.createPublicKey(it))
        .catch(err => {
            console.error('[ERROR] Failed to receive/decode public key!');
            throw err;
        });

const app = express();
const server = http.createServer(app);
const apollo = new ApolloServer({
    gateway,
    plugins: [ApolloServerPluginDrainHttpServer({ httpServer: server })],
});

await apollo.start();

app.use(
    '/',
    cors(),
    json(),
    expressjwt({
        secret: publicKey,
        algorithms: ['RS512'],
        credentialsRequired: false,
    }),
    expressMiddleware(apollo, {
        context: ({ req }) => ({
            currentUser: req.auth ? { id: req.auth.id, authorities: req.auth.authorities } : null,
            requestUuid: uuid.v4(),
        }),
    }),
);

server.listen({ port: gatewayPort }, () => {
    const { port } = server.address();
    const localUrl = `http://localhost:${port}`;

    console.log('🚀 Server ready at', localUrl);
});
