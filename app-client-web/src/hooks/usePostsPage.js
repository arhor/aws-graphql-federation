import { useEffect } from 'react';
import { useTranslation } from 'react-i18next';

import { useSnackbar } from 'notistack';

import { useGetPostsPageQuery } from '@/api/posts-api';

const GET_POSTS_OPTIONS = {
    pollingInterval: 60_000,
};

export default function usePostsPage() {
    const { t } = useTranslation();
    const { enqueueSnackbar } = useSnackbar();
    const { isLoading, isError, error, data } = useGetPostsPageQuery({ page: 1, size: 20 }, GET_POSTS_OPTIONS);

    useEffect(() => {
        if (isError) {
            enqueueSnackbar(error.message || t('errors.snackbar.default'), {
                variant: 'error',
                autoHideDuration: 5000,
            });
        }
    }, [isError, error, enqueueSnackbar, t]);

    return { isLoading, data };
}
