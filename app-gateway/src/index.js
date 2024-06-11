import { GATEWAY_PORT } from '#src/constants.js';
import { createApollo } from '#src/set-up-apollo.js';
import { createServer } from '#src/set-up-server.js';

const apollo = await createApollo();
const server = await createServer(apollo);

await server.listen({ port: GATEWAY_PORT, host: '0.0.0.0' });
