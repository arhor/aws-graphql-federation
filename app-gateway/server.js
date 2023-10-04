import { ApolloServer } from '@apollo/server';
import { expressMiddleware } from '@apollo/server/express4';
import { ApolloServerPluginDrainHttpServer } from '@apollo/server/plugin/drainHttpServer';
import express from 'express';
import { expressjwt } from 'express-jwt';
import http from 'http';
import cors from 'cors';
import bodyParser from 'body-parser';
import * as uuid from 'uuid';
import { gateway } from './gateway.js';
import crypto from 'crypto';
import { usersServiceUrl } from "./variables.js";

const publicKey =
    await fetch(`${usersServiceUrl}/public-key`)
        .then(it => it.text())
        .then(it => crypto.createPublicKey(it))
        .catch(err => {
            console.error('[ERROR] Failed to receive/decode public key!');
            throw err;
        });

const app = express();
const httpServer = http.createServer(app);
const apolloServer = new ApolloServer({
    gateway,
    plugins: [ApolloServerPluginDrainHttpServer({ httpServer })],
});

await apolloServer.start();

app.use(
    '/',
    cors(),
    bodyParser.json(),
    expressjwt({
        secret: publicKey,
        algorithms: ['RS512'],
        credentialsRequired: false,
    }),
    expressMiddleware(apolloServer, {
        context: ({ req }) => ({
            currentUser: req.auth ? { id: req.auth.id, authorities: req.auth.authorities } : null,
            requestUuid: uuid.v4(),
        }),
    }),
);

await new Promise((resolve) => httpServer.listen({ port: 4000 }, resolve));

console.log(`🚀 Server ready at http://localhost:4000`);
