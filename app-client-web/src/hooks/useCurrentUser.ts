import { useEffect } from 'react';

import { useQuery } from '@apollo/client';
import { useSnackbar } from 'notistack';

import { graphql } from '@/gql';

const GET_CURRENT_USER_INFO = graphql(`
    query GetCurrentUserInfo {
        me {
            id
            authorities
            authenticated
        }
    }
`);

export default function useCurrentUser() {
    const { enqueueSnackbar } = useSnackbar();
    const { loading, error, data } = useQuery(GET_CURRENT_USER_INFO, {
        defaultOptions: {
            fetchPolicy: 'network-only',
        },
    });

    useEffect(() => {
        if (error) {
            enqueueSnackbar(error.message, {
                variant: 'error',
                autoHideDuration: 10_000,
            });
        }
    }, [error, enqueueSnackbar]);

    useEffect(() => {
        if (data?.me?.authenticated === false) {
            enqueueSnackbar('Current user is not authenticated', {
                variant: 'error',
                autoHideDuration: 10_000,
            });
        }
    }, [data, enqueueSnackbar]);

    return { loading, data };
}
