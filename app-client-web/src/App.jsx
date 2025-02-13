import { ErrorBoundary } from 'react-error-boundary';

import { SnackbarProvider } from 'notistack';

import ErrorWidget from '@/components/ErrorWidget';
import AppRouter from '@/router/AppRouter';
import AppStoreProvider from '@/store/AppStoreProvider';
import AppThemeProvider from '@/theme/AppThemeProvider';

export default function App() {
    return (
        <AppThemeProvider>
            <ErrorBoundary FallbackComponent={ErrorWidget}>
                <AppStoreProvider>
                    <SnackbarProvider preventDuplicate>
                        <AppRouter />
                    </SnackbarProvider>
                </AppStoreProvider>
            </ErrorBoundary>
        </AppThemeProvider>
    );
}
