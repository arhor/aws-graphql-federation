import { useEffect } from 'react';

import { useMutation } from '@apollo/client';
import { useSnackbar } from 'notistack';

import { graphql } from '@/gql';

const CREATE_POST = graphql(`
    mutation CreatePost($input: CreatePostInput!) {
        createPost(input: $input) {
            id
            userId
            title
            content
            tags
        }
    }
`);

const POST_FRAGMENT = graphql(`
    fragment postFields on Post {
        id
    }
`);

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
