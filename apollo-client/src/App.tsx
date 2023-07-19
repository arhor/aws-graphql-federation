import { SnackbarProvider } from 'notistack';
import { RouterProvider } from 'react-router-dom';

import CssBaseline from '@mui/material/CssBaseline';

import ApolloClientProvider from '@/client/ApolloClientProvider';
import ErrorBoundary from '@/components/ErrorBoundary';
import { router } from '@/router';
import AppThemeProvider from '@/theme/AppThemeProvider';

export default function App() {
    return (
        <AppThemeProvider>
            <CssBaseline />
            <ErrorBoundary>
                <ApolloClientProvider>
                    <SnackbarProvider preventDuplicate>
                        <RouterProvider router={router} />
                    </SnackbarProvider>
                </ApolloClientProvider>
            </ErrorBoundary>
        </AppThemeProvider>
    );
}
