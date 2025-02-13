import { useEffect } from 'react';
import { useTranslation } from 'react-i18next';

import { useSnackbar } from 'notistack';

import { useGetCurrentUserQuery } from '@/api/users-api';
import { MILLIS_IN_5_SECONDS } from '@/utils/time-utils';

const GET_CURRENT_USER_OPTIONS = {
    refetchOnMountOrArgChange: true,
    refetchOnReconnect: true,
    refetchOnFocus: true,
};

export default function useCurrentUser() {
    const { t } = useTranslation();
    const { enqueueSnackbar } = useSnackbar();
    const { isLoading, isError, error, data } = useGetCurrentUserQuery(null, GET_CURRENT_USER_OPTIONS);

    useEffect(() => {
        if (isError) {
            enqueueSnackbar(error.message || t('errors.snackbar.default'), {
                variant: 'error',
                autoHideDuration: MILLIS_IN_5_SECONDS,
            });
        }
    }, [isError, error, enqueueSnackbar, t]);

    useEffect(() => {
        if (data?.currentUser?.authenticated === false) {
            enqueueSnackbar(t('Current user is not authenticated'), {
                variant: 'error',
                autoHideDuration: MILLIS_IN_5_SECONDS,
            });
        }
    }, [data, enqueueSnackbar, t]);

    return { isLoading, data };
}
