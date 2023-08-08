import { useEffect } from 'react';

import { useMutation, gql } from '@apollo/client';
import { useSnackbar } from 'notistack';

const CREATE_POST = gql`
    mutation CreatePost($input: CreatePostInput!) {
        createPost(input: $input) {
            id
            userId
            header
            banner
            content
            options
            tags
        }
    }
`;

const POST_FRAGMENT = gql`
    fragment postFields on Post {
        id
    }
`;

export default function useCreatePostMutation() {
    const { enqueueSnackbar } = useSnackbar();

    const [createPost, { error }] = useMutation(CREATE_POST, {
        update(cache, result) {
            cache.modify({
                fields: {
                    posts(existingPosts = []) {
                        return [
                            ...existingPosts,
                            cache.writeFragment({
                                data: result.data?.createPost,
                                fragment: POST_FRAGMENT,
                            }),
                        ];
                    },
                },
            });
        },
    });

    useEffect(() => {
        if (error) {
            enqueueSnackbar(error.message, {
                variant: 'error',
                autoHideDuration: 5000,
            });
        }
    }, [error, enqueueSnackbar]);

    return { createPost };
}
