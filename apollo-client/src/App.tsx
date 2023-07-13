import { ApolloClient, createHttpLink, InMemoryCache, ApolloProvider } from '@apollo/client';
import { setContext } from '@apollo/client/link/context';
import { SnackbarProvider } from 'notistack';

import CssBaseline from '@mui/material/CssBaseline';

import AppLayout from '@/AppLayout';
import AppThemeProvider from '@/AppThemeProvider';
import ErrorBoundary from '@/components/ErrorBoundary';

const httpLink = createHttpLink({
    uri: '/graphql',
});

const authLink = setContext((_, { headers }) => {
    const token = localStorage.getItem('token');
    return {
        headers: {
            ...headers,
            authorization: token ? `Bearer ${token}` : '',
        }
    }
});

const client = new ApolloClient({
    link: authLink.concat(httpLink),
    cache: new InMemoryCache(),
});

const App = () => (
    <AppThemeProvider>
        <CssBaseline />
        <ErrorBoundary>
            <ApolloProvider client={client}>
                <SnackbarProvider preventDuplicate>
                    <AppLayout />
                </SnackbarProvider>
            </ApolloProvider>
        </ErrorBoundary>
    </AppThemeProvider>
);

export default App;
