import { useEffect } from 'react';

import { useMutation, gql } from '@apollo/client';
import { useSnackbar } from 'notistack';

const AUTHENTICATE = gql`
    mutation Authenticate($input: AuthenticationInput!) {
        authenticate(input: $input) {
            accessToken
        }
    }
`;

export default function useAuthenticateMutation() {
    const { enqueueSnackbar } = useSnackbar();
    const [authenticate, { error }] = useMutation(AUTHENTICATE, {
        onCompleted(data) {
            localStorage.setItem('token', data.authenticate.accessToken);
        },
    });

    useEffect(() => {
        if (error) {
            enqueueSnackbar(error.message, {
                variant: 'error',
                autoHideDuration: 5000,
            });
        }
    }, [error, enqueueSnackbar]);

    return { authenticate };
}
