import { ReactNode } from 'react';

import { ApolloClient, ApolloProvider, createHttpLink, InMemoryCache } from '@apollo/client';

const client = new ApolloClient({
    link: createHttpLink({
        uri: '/graphql',
    }),
    cache: new InMemoryCache(),
    credentials: 'include',
});

/**
 * @param {Object} props
 * @param {ReactNode} props.children
 */
export default function AppGqlClientProvider(props) {
    return (
        <ApolloProvider client={client}>
            {props.children}
        </ApolloProvider>
    );
}
