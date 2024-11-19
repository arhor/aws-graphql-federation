import { ReactNode } from 'react';

import { SnackbarProvider } from 'notistack';

/**
 * @param {Object} props
 * @param {ReactNode} props.children
 */
export default function AppSnackbarProvider(props) {
    return (
        <SnackbarProvider preventDuplicate>
            {props.children}
        </SnackbarProvider>
    );
}
