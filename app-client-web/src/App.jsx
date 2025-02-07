import { ErrorBoundary } from 'react-error-boundary';

import { ApolloClient, ApolloProvider, createHttpLink, InMemoryCache } from '@apollo/client';
import { SnackbarProvider } from 'notistack';

import ErrorWidget from '@/components/ErrorWidget';
import AppRouter from '@/router/AppRouter';
import AppStoreProvider from '@/store/AppStoreProvider';
import AppThemeProvider from '@/theme/AppThemeProvider';

const client = new ApolloClient({
    link: createHttpLink({
        uri: '/graphql',
    }),
    cache: new InMemoryCache(),
    credentials: 'include',
});

export default function App() {
    return (
        <AppThemeProvider>
            <ErrorBoundary FallbackComponent={ErrorWidget}>
                <AppStoreProvider>
                    <ApolloProvider client={client}>
                        <SnackbarProvider preventDuplicate>
                            <AppRouter />
                        </SnackbarProvider>
                    </ApolloProvider>
                </AppStoreProvider>
            </ErrorBoundary>
        </AppThemeProvider>
    );
}
