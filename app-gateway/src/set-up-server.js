import * as uuid from 'uuid';
import fastify from 'fastify';
import fastifyCookie from '@fastify/cookie';
import fastifyJwt from '@fastify/jwt';
import fastifyOauth2 from '@fastify/oauth2';

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

//    server.register(fastifyOauth2, {
//        name: 'googleOAuth2',
//        scope: ['profile'],
//        credentials: {
//            client: {
//                id: '<CLIENT_ID>',
//                secret: '<CLIENT_SECRET>'
//            },
//            auth: oauthPlugin.GOOGLE_CONFIGURATION
//        },
//        startRedirectPath: '/login/google',
//        callbackUri: 'http://localhost:4000/login/google/callback'
//    });
//
//    server.get('/login/google/callback', function (request, reply) {
//      this.googleOAuth2.getAccessTokenFromAuthorizationCodeFlow(request, (err, result) => {
//        if (err) {
//          reply.send(err)
//          return
//        }
//
//        sget.concat({
//          url: 'https://www.googleapis.com/oauth2/v2/userinfo',
//          method: 'GET',
//          headers: {
//            Authorization: 'Bearer ' + result.token.access_token
//          },
//          json: true
//        }, function (err, _res, data) {
//          if (err) {
//            reply.send(err)
//            return
//          }
//          reply.send(data)
//        })
//      })
//    })
//
//    fastify.register(oauthPlugin, {
//      name: 'githubOAuth2',
//      scope: [],
//      credentials: {
//        client: {
//          id: '<CLIENT_ID>',
//          secret: '<CLIENT_SECRET>'
//        },
//        auth: oauthPlugin.GITHUB_CONFIGURATION
//      },
//      startRedirectPath: '/login/github',
//      callbackUri: 'http://localhost:3000/login/github/callback'
//    })
//
//    const memStore = new Map()
//
//    async function saveAccessToken (token) {
//      memStore.set(token.refresh_token, token)
//    }
//
//    async function retrieveAccessToken (token) {
//      // remove Bearer if needed
//      if (token.startsWith('Bearer ')) {
//        token = token.substring(6)
//      }
//      // any database or in-memory operation here
//      // we use in-memory variable here
//      if (memStore.has(token)) {
//        memStore.get(token)
//      }
//      throw new Error('invalid refresh token')
//    }
//
//    fastify.get('/login/github/callback', async function (request, reply) {
//      const token = await this.githubOAuth2.getAccessTokenFromAuthorizationCodeFlow(request)
//
//      console.log(token.access_token)
//
//      // you should store the `token` for further usage
//      await saveAccessToken(token)
//
//      reply.send({ access_token: token.access_token })
//    })
//
//    fastify.get('/login/github/refreshAccessToken', async function (request, reply) {
//      // we assume the token is passed by authorization header
//      const refreshToken = await retrieveAccessToken(request.headers.authorization)
//      const newToken = await this.githubOAuth2.getAccessTokenFromRefreshToken(refreshToken, {})
//
//      // we save the token again
//      await saveAccessToken(newToken)
//
//      reply.send({ access_token: newToken.access_token })
//    })
//
//    // Check access token: https://docs.github.com/en/rest/apps/oauth-applications#check-a-token
//    fastify.get('/login/github/verifyAccessToken', function (request, reply) {
//      const { accessToken } = request.query
//
//      sget.concat(
//        {
//          url: 'https://api.github.com/applications/<CLIENT_ID>/token',
//          method: 'POST',
//          headers: {
//            Authorization:
//              'Basic ' +
//              Buffer.from('<CLIENT_ID>' + ':' + '<CLIENT_SECRET').toString(
//                'base64'
//              )
//          },
//          body: JSON.stringify({ access_token: accessToken }),
//          json: true
//        },
//        function (err, _res, data) {
//          if (err) {
//            reply.send(err)
//            return
//          }
//          reply.send(data)
//        }
//      )
//    })

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
