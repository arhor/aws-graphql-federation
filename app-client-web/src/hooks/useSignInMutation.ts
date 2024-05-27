import { useEffect } from 'react';

import { gql, useMutation } from '@apollo/client';
import { useSnackbar } from 'notistack';

const AUTHENTICATE = gql`
    mutation SignIn($input: SignInInput!) {
        signIn(input: $input) {
            accessToken
        }
    }
`;

export default function useSignInMutation() {
    const { enqueueSnackbar } = useSnackbar();
    const [signIn, { error }] = useMutation(AUTHENTICATE, {
        onCompleted(data) {
            localStorage.setItem('accessToken', data.signIn.accessToken);
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

    return { signIn };
}
