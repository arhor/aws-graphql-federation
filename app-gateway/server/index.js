import { createApollo } from '#server/createApollo.js';
import { createServer } from '#server/createServer.js';
import { GATEWAY_PORT } from '#server/utils/constants.js';

const apollo = await createApollo();
const server = await createServer(apollo);

await server.listen({ port: GATEWAY_PORT, host: '0.0.0.0' });
