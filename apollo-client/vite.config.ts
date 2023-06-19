import { fileURLToPath, URL } from 'url';
import dns from 'dns';

import react from '@vitejs/plugin-react';
import { defineConfig, loadEnv } from 'vite';
import eslint from 'vite-plugin-eslint';

dns.setDefaultResultOrder('verbatim');

export default defineConfig(({ mode }) => {
    const rootProjectDir = fileURLToPath(new URL('..', import.meta.url));
    const variablePrefixes = [''];

    console.log('--------------------------');
    console.log(rootProjectDir);
    console.log(mode);
    
    console.log('--------------------------');

    process.env = { ...loadEnv(mode, rootProjectDir, variablePrefixes) };

    return {
        plugins: [
            react(),
            eslint(),
        ],
        resolve: {
            alias: {
                '@': fileURLToPath(new URL('src', import.meta.url)),
            },
        },
        server: {
            proxy: {
                '^/(api|graphql)': {
                    target: process.env.API_BASE_URL ?? (() => {
                        throw new Error('Environment variable API_BASE_URL is missing');
                    })(),
                    changeOrigin: true,
                },
            },
        },
        build: {
            outDir: 'build/dist',
        },
        test: {
            globals: true,
            environment: 'jsdom',
            watch: false,
            setupFiles: ['src/tests.setup.ts'],
        },
    }
});
