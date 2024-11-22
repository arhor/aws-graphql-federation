import { RouterProvider } from 'react-router-dom';

import { ApolloClient, ApolloProvider, createHttpLink, InMemoryCache } from '@apollo/client';
import { SnackbarProvider } from 'notistack';

import ErrorBoundary from '@/components/ErrorBoundary';
import { router } from '@/router';
import { AppThemeProvider } from '@/theme';

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
            <ErrorBoundary>
                <ApolloProvider client={client}>
                    <SnackbarProvider preventDuplicate>
                        <RouterProvider router={router} />
                    </SnackbarProvider>
                </ApolloProvider>
            </ErrorBoundary>
        </AppThemeProvider>
    );
}
