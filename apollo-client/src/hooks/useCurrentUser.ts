import { useQuery } from '@apollo/client';

import { graphql } from '@/gql';

const GET_CURRENT_USER = graphql(`
    query GetCurrentUser {
        currentUser {
            id
            username
            settings
        }
    }
`);

const useCurrentUser = () => {
    const { loading, data } = useQuery(GET_CURRENT_USER, { pollInterval: 60_000 });

    return { loading, data };
};

export default useCurrentUser;
