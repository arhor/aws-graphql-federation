import { gql } from 'graphql-request';

import graphqlBaseApi from '@/api/graphql-base-api';
import { CURRENT_USER } from '@/api/tags';

const usersApi = graphqlBaseApi.enhanceEndpoints({
    addTagTypes: [
        CURRENT_USER,
    ]
}).injectEndpoints({
    endpoints: (builder) => ({
        GetCurrentUser: builder.query({
            query: () => ({
                document: gql`
                    query GetCurrentUser {
                        currentUser: me {
                            id
                            authorities
                            authenticated
                        }
                    }
                `,
            }),
            providesTags: [CURRENT_USER]
        }),

        GetUserById: builder.query({
            query: ({ id }) => ({
                document: gql`
                    query GetUserById($id: UUID!) {
                        user(id: $id) {
                            id
                        }
                    }
                `,
                variables: {
                    id,
                },
            }),
        }),

        GetUsersPage: builder.query({
            query: ({ page, size }) => ({
                document: gql`
                    query GetUsersPage($page: Int!, $size: Int!) {
                        users(input: { page: $page, size: $size }) {
                            data {
                                id
                            }
                            page
                            size
                            hasPrev
                            hasNext
                        }
                    }
                `,
                variables: {
                    page,
                    size,
                },
            }),
        }),

        CreateUser: builder.mutation({
            mutation: ({ username, password }) => ({
                document: gql`
                    mutation CreateUser($input: CreateUserInput!) {
                        createUser(input: $input) {
                            id
                        }
                    }
                `,
                variables: {
                    input: {
                        username,
                        password,
                    },
                },
            }),
        }),

        UpdateUser: builder.mutation({
            mutation: ({ id, password }) => ({
                document: gql`
                    mutation UpdateUser($input: UpdateUserInput!) {
                        updateUser(input: $input) {
                            id
                        }
                    }
                `,
                variables: {
                    input: {
                        id,
                        password,
                    },
                },
            }),
        }),

        DeleteUser: builder.mutation({
            mutation: ({ id }) => ({
                document: gql`
                    mutation DeleteUser(id: UUID!) {
                        deleteUser(id: $id)
                    }
                `,
                variables: {
                    id,
                },
            }),
        }),
    }),
});

export default usersApi;

export const {
    useGetCurrentUserQuery,
    useGetUserByIdQuery,
    useGetUsersPageQuery,
    useCreateUserMutation,
    useUpdateUserMutation,
    useDeleteUserMutation,
} = usersApi;
