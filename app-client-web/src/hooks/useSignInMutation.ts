import { useEffect } from 'react';

import { useMutation } from '@apollo/client';
import { useSnackbar } from 'notistack';

import { graphql } from '@/gql';

const AUTHENTICATE = graphql(`
    mutation SignIn($input: SignInInput!) {
        signIn(input: $input) {
            accessToken
        }
    }
`);

export default function useSignInMutation() {
    const { enqueueSnackbar } = useSnackbar();
    const [signIn, { error }] = useMutation(AUTHENTICATE, {
        onCompleted(data) {
            if (data?.signIn?.accessToken) {
                localStorage.setItem('accessToken', data.signIn.accessToken);
            }
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
