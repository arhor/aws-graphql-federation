import { useEffect } from 'react';

import { useMutation } from '@apollo/client';
import { useSnackbar } from 'notistack';

import { graphql } from '@/gql';

const CREATE_TOPIC = graphql(`
    mutation CreateTopic($name: String!, $userId: Long!) {
        createTopic(
            request: {
                name: $name, 
                userId: $userId
            }
        ) {
            id
            userId
            name
        }
    }
`);

const TOPIC_FRAGMENT = graphql(`
    fragment NewTopic on Topic {
        id
        userId
        name
    }
`);

const useCreateTopicMutation = () => {
    const { enqueueSnackbar } = useSnackbar();

    const [createTopic, { error }] = useMutation(CREATE_TOPIC, {
        update(cache, result) {
            cache.modify({
                fields: {
                    topics(existingTopics = []) {
                        return [
                            ...existingTopics,
                            cache.writeFragment({
                                data: result.data?.createTopic,
                                fragment: TOPIC_FRAGMENT,
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
                autoHideDuration: 5_000,
            });
        }
    }, [error, enqueueSnackbar]);

    return { createTopic };
};

export default useCreateTopicMutation;
