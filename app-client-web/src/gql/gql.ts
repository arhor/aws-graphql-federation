/* eslint-disable */
import * as types from './graphql';
import { TypedDocumentNode as DocumentNode } from '@graphql-typed-document-node/core';

/**
 * Map of all GraphQL operations in the project.
 *
 * This map has several performance disadvantages:
 * 1. It is not tree-shakeable, so it will include all operations in the project.
 * 2. It is not minifiable, so the string of a GraphQL query will be multiple times inside the bundle.
 * 3. It does not support dead code elimination, so it will add unused operations.
 *
 * Therefore it is highly recommended to use the babel or swc plugin for production.
 */
const documents = {
    "\n    mutation CreatePost($input: CreatePostInput!) {\n        createPost(input: $input) {\n            id\n            userId\n            title\n            content\n            tags\n        }\n    }\n": types.CreatePostDocument,
    "\n    fragment postFields on Post {\n        id\n    }\n": types.PostFieldsFragmentDoc,
    "\n    mutation CreateUser($input: CreateUserInput!) {\n        createUser(input: $input) {\n            id\n            username\n        }\n    }\n": types.CreateUserDocument,
    "\n    fragment NewUser on User {\n        id\n        username\n    }\n": types.NewUserFragmentDoc,
    "\n    query GetPostsPage($page: Int!, $size: Int!) {\n        posts(input: { page: $page, size: $size }) {\n            data {\n                id\n                title\n                tags\n            }\n        }\n    }\n": types.GetPostsPageDocument,
    "\n    mutation SignIn($input: SignInInput!) {\n        signIn(input: $input)\n    }\n": types.SignInDocument,
    "\n    mutation SignOut {\n        signOut\n    }\n": types.SignOutDocument,
};

/**
 * The graphql function is used to parse GraphQL queries into a document that can be used by GraphQL clients.
 *
 *
 * @example
 * ```ts
 * const query = graphql(`query GetUser($id: ID!) { user(id: $id) { name } }`);
 * ```
 *
 * The query argument is unknown!
 * Please regenerate the types.
 */
export function graphql(source: string): unknown;

/**
 * The graphql function is used to parse GraphQL queries into a document that can be used by GraphQL clients.
 */
export function graphql(source: "\n    mutation CreatePost($input: CreatePostInput!) {\n        createPost(input: $input) {\n            id\n            userId\n            title\n            content\n            tags\n        }\n    }\n"): (typeof documents)["\n    mutation CreatePost($input: CreatePostInput!) {\n        createPost(input: $input) {\n            id\n            userId\n            title\n            content\n            tags\n        }\n    }\n"];
/**
 * The graphql function is used to parse GraphQL queries into a document that can be used by GraphQL clients.
 */
export function graphql(source: "\n    fragment postFields on Post {\n        id\n    }\n"): (typeof documents)["\n    fragment postFields on Post {\n        id\n    }\n"];
/**
 * The graphql function is used to parse GraphQL queries into a document that can be used by GraphQL clients.
 */
export function graphql(source: "\n    mutation CreateUser($input: CreateUserInput!) {\n        createUser(input: $input) {\n            id\n            username\n        }\n    }\n"): (typeof documents)["\n    mutation CreateUser($input: CreateUserInput!) {\n        createUser(input: $input) {\n            id\n            username\n        }\n    }\n"];
/**
 * The graphql function is used to parse GraphQL queries into a document that can be used by GraphQL clients.
 */
export function graphql(source: "\n    fragment NewUser on User {\n        id\n        username\n    }\n"): (typeof documents)["\n    fragment NewUser on User {\n        id\n        username\n    }\n"];
/**
 * The graphql function is used to parse GraphQL queries into a document that can be used by GraphQL clients.
 */
export function graphql(source: "\n    query GetPostsPage($page: Int!, $size: Int!) {\n        posts(input: { page: $page, size: $size }) {\n            data {\n                id\n                title\n                tags\n            }\n        }\n    }\n"): (typeof documents)["\n    query GetPostsPage($page: Int!, $size: Int!) {\n        posts(input: { page: $page, size: $size }) {\n            data {\n                id\n                title\n                tags\n            }\n        }\n    }\n"];
/**
 * The graphql function is used to parse GraphQL queries into a document that can be used by GraphQL clients.
 */
export function graphql(source: "\n    mutation SignIn($input: SignInInput!) {\n        signIn(input: $input)\n    }\n"): (typeof documents)["\n    mutation SignIn($input: SignInInput!) {\n        signIn(input: $input)\n    }\n"];
/**
 * The graphql function is used to parse GraphQL queries into a document that can be used by GraphQL clients.
 */
export function graphql(source: "\n    mutation SignOut {\n        signOut\n    }\n"): (typeof documents)["\n    mutation SignOut {\n        signOut\n    }\n"];

export function graphql(source: string) {
  return (documents as any)[source] ?? {};
}

export type DocumentType<TDocumentNode extends DocumentNode<any, any>> = TDocumentNode extends DocumentNode<  infer TType,  any>  ? TType  : never;