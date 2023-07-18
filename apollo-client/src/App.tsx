import { ApolloProvider } from '@apollo/client';
import { SnackbarProvider } from 'notistack';
import { RouterProvider } from 'react-router-dom';

import CssBaseline from '@mui/material/CssBaseline';

import { apolloClient } from '@/client';
import ErrorBoundary from '@/components/ErrorBoundary';
import { router } from '@/router.tsx';
import AppThemeProvider from '@/theme/AppThemeProvider';

function App() {
    return (
        <AppThemeProvider>
            <CssBaseline />
            <ErrorBoundary>
                <ApolloProvider client={apolloClient}>
                    <SnackbarProvider preventDuplicate>
                        <RouterProvider router={router} />
                    </SnackbarProvider>
                </ApolloProvider>
            </ErrorBoundary>
        </AppThemeProvider>
    );
}

export default App;
