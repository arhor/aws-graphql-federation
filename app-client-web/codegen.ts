import type { CodegenConfig } from '@graphql-codegen/cli';

export default {
    overwrite: true,
    schema: 'supergraph.graphql',
    documents: [
        'src/**/*.{ts,tsx}',
    ],
    generates: {
        'src/gql/': {
            preset: 'client',
            config: {
                strictScalars: true,
                scalars: {
                    Long: 'number',
                    UUID: 'string',
                    Object: '{ [key: string]: any }',
                }
            }
        },
    },
} as CodegenConfig;
