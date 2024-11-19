import { useEffect } from 'react';

import { gql, useMutation } from '@apollo/client';
import { useSnackbar } from 'notistack';

const SIGN_OUT = gql`
    mutation SignOut {
        signOut
    }
`;

export default function useSignOutMutation() {
    const { enqueueSnackbar } = useSnackbar();
    const [signOut, { error }] = useMutation(SIGN_OUT);

    useEffect(() => {
        if (error) {
            enqueueSnackbar(error.message, {
                variant: 'error',
                autoHideDuration: 5000,
            });
        }
    }, [error, enqueueSnackbar]);

    return { signOut };
}
