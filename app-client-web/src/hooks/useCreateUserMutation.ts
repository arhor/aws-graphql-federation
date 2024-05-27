import { useEffect } from 'react';

import { useMutation, gql } from '@apollo/client';
import { useSnackbar } from 'notistack';

const CREATE_USER = gql`
    mutation CreateUser($input: CreateUserInput!) {
        createUser(input: $input) {
            id
            username
        }
    }
`;

const USER_FRAGMENT = gql`
    fragment NewUser on User {
        id
        username
    }
`;

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
