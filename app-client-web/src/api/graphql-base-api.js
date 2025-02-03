import { createApi } from '@reduxjs/toolkit/query/react';
import { graphqlRequestBaseQuery } from '@rtk-query/graphql-request-base-query';

export default createApi({
    baseQuery: graphqlRequestBaseQuery({
        url: '/graphql',
    }),
    endpoints: () => ({}),
});