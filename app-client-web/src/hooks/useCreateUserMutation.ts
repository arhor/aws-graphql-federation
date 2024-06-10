import { useEffect } from 'react';

import { useMutation } from '@apollo/client';
import { useSnackbar } from 'notistack';

import { graphql } from '@/gql';

const CREATE_USER = graphql(`
    mutation CreateUser($input: CreateUserInput!) {
        createUser(input: $input) {
            id
            username
        }
    }
`);

const USER_FRAGMENT = graphql(`
    fragment NewUser on User {
        id
        username
    }
`);

export default function useCreateUserMutation() {
    const { enqueueSnackbar } = useSnackbar();
    const [createUser, { error }] = useMutation(CREATE_USER, {
        update(cache, result) {
            cache.modify({
                fields: {
                    users(existingUsers = []) {
                        return [
                            ...existingUsers,
                            cache.writeFragment({
                                data: result.data?.createUser,
                                fragment: USER_FRAGMENT,
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

    return { createUser };
}
