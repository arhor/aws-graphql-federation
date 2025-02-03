import { Provider } from 'react-redux'

import { ApolloClient, ApolloProvider, createHttpLink, InMemoryCache } from '@apollo/client';
import { SnackbarProvider } from 'notistack';

import { ErrorBoundary } from '@/components';
import { AppRouter } from '@/router';
import { store } from '@/store';
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
                <Provider store={store}>
                    <ApolloProvider client={client}>
                        <SnackbarProvider preventDuplicate>
                            <AppRouter />
                        </SnackbarProvider>
                    </ApolloProvider>
                </Provider>
            </ErrorBoundary>
        </AppThemeProvider>
    );
}
