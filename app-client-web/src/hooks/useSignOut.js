import { useEffect } from 'react';
import { useTranslation } from 'react-i18next';

import { useSnackbar } from 'notistack';

import { useSignOutMutation } from '@/api/auth-api';

export default function useSignOut() {
    const { t } = useTranslation();
    const { enqueueSnackbar } = useSnackbar();
    const [signOut, { isLoading, isError, error }] = useSignOutMutation();

    useEffect(() => {
        if (isError) {
            enqueueSnackbar(error.message || t('errors.snackbar.default'), {
                variant: 'error',
                autoHideDuration: 5000,
            });
        }
    }, [isError, error, enqueueSnackbar, t]);

    return { signOut, isLoading };
}
