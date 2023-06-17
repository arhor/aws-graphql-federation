import '@/styles/globals.css'
import '@fontsource/roboto/300.css';
import '@fontsource/roboto/400.css';
import '@fontsource/roboto/500.css';
import '@fontsource/roboto/700.css';
import { ThemeProvider } from '@mui/material';
import { createTheme } from '@mui/material/styles';
import createCache from '@emotion/cache';
import { CacheProvider } from '@emotion/react';
import type { AppProps } from 'next/app'

export const emotionCache = createCache({
    key: 'css',
    prepend: true,
});

export const theme = createTheme({
    palette: {
        primary: {
            main: '#fcba03',
        },
    },
});

export default function App({ Component, pageProps }: AppProps) {
    return (
        <CacheProvider value={emotionCache}>
            <ThemeProvider theme={theme}>
                <Component {...pageProps} />
            </ThemeProvider>
        </CacheProvider>
    )
}
