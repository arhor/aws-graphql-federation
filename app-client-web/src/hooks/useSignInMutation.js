import { useEffect } from 'react';

import { gql, useMutation } from '@apollo/client';
import { useSnackbar } from 'notistack';

const SIGN_IN = gql`
    mutation SignIn($input: SignInInput!) {
        signIn(input: $input)
    }
`;

export default function useSignInMutation() {
    const { enqueueSnackbar } = useSnackbar();
    const [signIn, { error }] = useMutation(SIGN_IN);

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
