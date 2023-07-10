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
        secret: '2VXAh+LCSh9lzKV/7djiYzeqjjV05JjuLoXJNOZv6M4pzERH+sGEC4VJXqoQSbIhtUBlOs5rYFR+limfmtu3TvwMFj/BrN2qHOvXUXbr1v0=',
        algorithms: ['HS256', 'HS512'],
        credentialsRequired: false,
    }),
    expressMiddleware(apolloServer, {
        context: ({ req }) => {
            console.log(req.auth);
            return {
                currentUser: req.auth ? (() => ({ id: req.auth.id, authorities: req.auth.authorities }))() : null,
                requestId: uuid.v4(),
            };
        },
    }),
);

await new Promise((resolve) => httpServer.listen({ port: 4000 }, resolve));

console.log(`🚀 Server ready at http://localhost:4000`);
