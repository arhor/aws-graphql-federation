import type { CodegenConfig } from '@graphql-codegen/cli';

const separator = '=';
const argumentName = '--schema-path';
const locationPath = process.argv.find(arg => arg.startsWith(argumentName + separator))?.split(separator)?.[1];

if (!locationPath) {
    throw new Error(`Argument '${argumentName}' must be specified`);
}

const config: CodegenConfig = {
    overwrite: true,
    schema: [locationPath],
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
                    Object: '{ [key: string]: any }',
                    Settings: 'string[]',
                }
            }
        },
    },
};

export default config;
