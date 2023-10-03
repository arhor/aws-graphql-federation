import { useEffect } from 'react';

import { useQuery } from '@apollo/client';
import { useSnackbar } from 'notistack';

import { graphql } from '@/gql';

const GET_TOPICS = graphql(`
    query GetTopics {
        topics {
            id
            name
            posts {
                id
                content
            }
        }
    }
`);

const useTopics = () => {
    const { enqueueSnackbar } = useSnackbar();
    const { loading, error, data, previousData } = useQuery(GET_TOPICS, { pollInterval: 60_000 });

    useEffect(() => {
        if (error) {
            enqueueSnackbar(error.message, {
                variant: 'error',
                autoHideDuration: 10_000,
            });
        }
    }, [error, enqueueSnackbar]);

    return { loading, data, previousData };
};

export default useTopics;
