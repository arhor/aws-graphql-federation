import { useEffect } from 'react';

import { useQuery } from '@apollo/client';
import { useSnackbar } from 'notistack';

import { graphql } from '@/gql';

const GET_POSTS_PAGE = graphql(`
    query GetPostsPage($page: Int!, $size: Int!) {
        posts(input: { page: $page, size: $size }) {
            data {
                id
                title
                tags
            }
        }
    }
`);

export default function usePostsPage() {
    const { enqueueSnackbar } = useSnackbar();
    const { loading, error, data, previousData } = useQuery(GET_POSTS_PAGE, {
        variables: {
            page: 0,
            size: 20,
        },
        pollInterval: 60_000,
    });

    useEffect(() => {
        if (error) {
            enqueueSnackbar(error.message, {
                variant: 'error',
                autoHideDuration: 10_000,
            });
        }
    }, [error, enqueueSnackbar]);

    return { loading, data, previousData };
}
