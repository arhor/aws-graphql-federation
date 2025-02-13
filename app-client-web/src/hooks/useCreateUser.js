import { useEffect } from 'react';
import { useTranslation } from 'react-i18next';

import { useSnackbar } from 'notistack';

import { useCreateUserMutation } from '@/api/users-api';

export default function useCreateUser() {
    const { t } = useTranslation();
    const { enqueueSnackbar } = useSnackbar();
    const [createUser, { isLoading, isError, error, data }] = useCreateUserMutation();

    useEffect(() => {
        if (isError) {
            enqueueSnackbar(error.message || t('errors.snackbar.default'), {
                variant: 'error',
                autoHideDuration: 5000,
            });
        }
    }, [isError, error, enqueueSnackbar, t]);

    return { createUser, isLoading, data };
}
