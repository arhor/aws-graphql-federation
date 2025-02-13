import { useEffect } from 'react';
import { useTranslation } from 'react-i18next';

import { useSnackbar } from 'notistack';

import { useSignInMutation } from '@/api/auth-api';

export default function useSignIn() {
    const { t } = useTranslation();
    const { enqueueSnackbar } = useSnackbar();
    const [signIn, { isLoading, isError, error }] = useSignInMutation();

    useEffect(() => {
        if (isError) {
            enqueueSnackbar(error.message || t('errors.snackbar.default'), {
                variant: 'error',
                autoHideDuration: 5000,
            });
        }
    }, [isError, error, enqueueSnackbar,t ]);

    return { signIn, isLoading };
}
