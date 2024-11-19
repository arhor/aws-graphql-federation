// noinspection JSUnusedGlobalSymbols

import { fileURLToPath, URL } from 'url';
import dns from 'dns';

import react from '@vitejs/plugin-react';
import { defineConfig, loadEnv } from 'vite';

dns.setDefaultResultOrder('verbatim');

export default defineConfig(({ mode }) => {
    const rootProjectDir = fileURLToPath(new URL('..', import.meta.url));
    const variablePrefixes = [''];

    process.env = { ...loadEnv(mode, rootProjectDir, variablePrefixes) };

    return {
        plugins: [
            react(),
        ],
        resolve: {
            alias: {
                '@': fileURLToPath(new URL('src', import.meta.url)),
            },
        },
        server: {
            proxy: {
                '^/(graphql)': {
                    target: 'http://localhost:4000',
                    changeOrigin: true,
                },
            },
        },
        build: {
            rollupOptions: {
                output: {
                    manualChunks: {
                        react: [
                            'react',
                            'react-dom',
                            'react-i18next',
                            'react-router',
                            'react-router-dom',
                        ],
                        material: [
                            '@mui/icons-material',
		                    '@mui/material',
                        ],
                    },
                },
            },
        },
        test: {
            globals: true,
            environment: 'jsdom',
            watch: false,
            setupFiles: ['src/tests.setup.ts'],
        },
    }
});
