import { useEffect } from 'react';
import { useTranslation } from 'react-i18next';

import { useSnackbar } from 'notistack';

import { useCreatePostMutation } from '@/api/posts-api';

export default function useCreatePost() {
    const { t } = useTranslation();
    const { enqueueSnackbar } = useSnackbar();
    const [createPost, { isLoading, isError, error, data }] = useCreatePostMutation();

    useEffect(() => {
        if (isError) {
            enqueueSnackbar(error.message || t('errors.snackbar.default'), {
                variant: 'error',
                autoHideDuration: 5000,
            });
        }
    }, [isError, error, enqueueSnackbar, t]);

    return { createPost, isLoading, data };
}
