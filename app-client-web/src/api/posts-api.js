import { gql } from 'graphql-request';

import graphqlBaseApi from '@/api/graphql-base-api';

const postsApi = graphqlBaseApi.injectEndpoints({
    endpoints: (builder) => ({
        GetPostById: builder.query({
            query: ({ id }) => ({
                document: gql`
                    query GetPostById($id: UUID!) {
                        post(id: $id) {
                            id
                            title
                            tags
                        }
                    }
                `,
                variables: {
                    id,
                },
            }),
        }),

        GetPostsPage: builder.query({
            query: ({ page, size }) => ({
                document: gql`
                    query GetPostsPage($input: PostsLookupInput!) {
                        posts(input: $input) {
                            data {
                                id
                                title
                                tags
                            }
                        }
                        page
                        size
                        hasPrev
                        hasNext
                    }
                `,
                variables: {
                    input: {
                        page,
                        size,
                    },
                }
            }),
        }),

        CreatePost: builder.mutation({
            mutation: ({ title, content, tags }) => ({
                document: gql`
                    mutation CreatePost($input: CreatePostInput!) {
                        createPost(input: $input) {
                            id
                        }
                    }
                `,
                variables: {
                    input: {
                        title,
                        content,
                        tags,
                    },
                },
            }),
        }),

        UpdatePost: builder.mutation({
            mutation: ({ id, title, content, tags }) => ({
                document: gql`
                    mutation UpdatePost($input: UpdatePostInput!) {
                        updatePost(input: $input) {
                            id
                        }
                    }
                `,
                variables: {
                    input: {
                        id,
                        title,
                        content,
                        tags,
                    },
                },
            }),
        }),

        DeletePost: builder.mutation({
            mutation: ({ id }) => ({
                document: gql`
                    mutation DeletePost(id: UUID!) {
                        deletePost(id: $id)
                    }
                `,
                variables: {
                    id,
                },
            }),
        }),
    }),
});

export default postsApi;

export const { useGetPostsPageQuery } = postsApi;
