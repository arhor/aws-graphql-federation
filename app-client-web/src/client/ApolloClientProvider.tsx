import { ReactNode } from 'react';

import { ApolloClient, ApolloProvider, createHttpLink, InMemoryCache } from '@apollo/client';

const client = new ApolloClient({
    link: createHttpLink({
        uri: '/graphql',
    }),
    cache: new InMemoryCache(),
    credentials: 'include',
});

export default function ApolloClientProvider(props: { children: ReactNode }) {
    return (
        <ApolloProvider client={client}>
            {props.children}
        </ApolloProvider>
    );
}
