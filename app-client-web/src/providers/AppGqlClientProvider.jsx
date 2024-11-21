import { ApolloClient, ApolloProvider, createHttpLink, InMemoryCache } from '@apollo/client';
import PropTypes from 'prop-types';

const client = new ApolloClient({
    link: createHttpLink({
        uri: '/graphql',
    }),
    cache: new InMemoryCache(),
    credentials: 'include',
});

export default function AppGqlClientProvider(props) {
    return (
        <ApolloProvider client={client}>
            {props.children}
        </ApolloProvider>
    );
}

AppGqlClientProvider.propTypes = {
    children: PropTypes.element.isRequired,
}
