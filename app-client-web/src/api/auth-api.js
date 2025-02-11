import { gql } from 'graphql-request';

import graphqlBaseApi from '@/api/graphql-base-api';
import { CURRENT_USER } from '@/api/tags';

const authApi = graphqlBaseApi.injectEndpoints({
    endpoints: (builder) => ({
        SignIn: builder.mutation({
            mutation: ({ username, password }) => ({
                document: gql`
                    mutation SignIn($input: SignInInput!) {
                        signIn(input: $input)
                    }
                `,
                variables: {
                    input: {
                        username,
                        password,
                    }
                },
            }),
        }),

        SignOut: builder.mutation({
            mutation: () => ({
                document: gql`
                    mutation SignOut() {
                        signOut
                    }
                `,
            }),
            invalidatesTags: [CURRENT_USER]
        }),
    }),
});

export default authApi;

export const {
    useSignInMutation,
    useSignOutMutation,
} = authApi;
