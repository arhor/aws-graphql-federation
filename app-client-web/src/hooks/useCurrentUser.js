import { useEffect } from 'react';

import { gql, useQuery } from '@apollo/client';
import { useSnackbar } from 'notistack';

import { MILLIS_IN_5_SECONDS } from '@/utils/time-utils';

const GET_CURRENT_USER_INFO = gql`
    query GetCurrentUserInfo {
        currentUser: me {
            id
            authorities
            authenticated
        }
    }
`;

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
                autoHideDuration: MILLIS_IN_5_SECONDS,
            });
        }
    }, [error, enqueueSnackbar]);

    useEffect(() => {
        if (data?.currentUser?.authenticated === false) {
            enqueueSnackbar('Current user is not authenticated', {
                variant: 'error',
                autoHideDuration: MILLIS_IN_5_SECONDS,
            });
        }
    }, [data, enqueueSnackbar]);

    return { loading, data };
}
