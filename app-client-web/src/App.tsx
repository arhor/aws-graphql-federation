import { RouterProvider } from 'react-router-dom';

import ErrorBoundary from '@/components/ErrorBoundary';
import { AppGqlClientProvider, AppSnackbarProvider, AppThemeProvider } from '@/providers';
import { router } from '@/router';

export default function App() {
    return (
        <AppThemeProvider>
            <ErrorBoundary>
                <AppGqlClientProvider>
                    <AppSnackbarProvider>
                        <RouterProvider router={router} />
                    </AppSnackbarProvider>
                </AppGqlClientProvider>
            </ErrorBoundary>
        </AppThemeProvider>
    );
}
