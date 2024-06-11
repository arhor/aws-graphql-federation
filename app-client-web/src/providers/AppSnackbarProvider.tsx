import { ReactNode } from 'react';

import { SnackbarProvider } from 'notistack';

export default function AppSnackbarProvider(props: { children: ReactNode }) {
    return (
        <SnackbarProvider preventDuplicate>
            {props.children}
        </SnackbarProvider>
    );
}
