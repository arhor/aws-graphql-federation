/* eslint-disable */
import { TypedDocumentNode as DocumentNode } from '@graphql-typed-document-node/core';

export type Maybe<T> = T | null;
export type InputMaybe<T> = Maybe<T>;
export type Exact<T extends { [key: string]: unknown }> = { [K in keyof T]: T[K] };
export type MakeOptional<T, K extends keyof T> = Omit<T, K> & { [SubKey in K]?: Maybe<T[SubKey]> };
export type MakeMaybe<T, K extends keyof T> = Omit<T, K> & { [SubKey in K]: Maybe<T[SubKey]> };
export type MakeEmpty<T extends { [key: string]: unknown }, K extends keyof T> = { [_ in K]?: never };
export type Incremental<T> = T | { [P in keyof T]?: P extends ' $fragmentName' | '__typename' ? T[P] : never };
/** All built-in and custom scalars, mapped to their actual values */
export type Scalars = {
  ID: { input: string; output: string; }
  String: { input: string; output: string; }
  Boolean: { input: boolean; output: boolean; }
  Int: { input: number; output: number; }
  Float: { input: number; output: number; }
  /** A universally unique identifier compliant UUID Scalar */
  UUID: { input: string; output: string; }
};

/**  ---------- Domain Types ---------- */
export type Comment = {
  __typename?: 'Comment';
  content: Scalars['String']['output'];
  id: Scalars['UUID']['output'];
  postId: Scalars['UUID']['output'];
  prntId?: Maybe<Scalars['UUID']['output']>;
  replies?: Maybe<Array<Comment>>;
  userId?: Maybe<Scalars['UUID']['output']>;
};

/**  ---------- Input Types ---------- */
export type CreateCommentInput = {
  content: Scalars['String']['input'];
  postId: Scalars['UUID']['input'];
  prntId?: InputMaybe<Scalars['UUID']['input']>;
  userId: Scalars['UUID']['input'];
};

export type CreatePostInput = {
  content: Scalars['String']['input'];
  tags?: InputMaybe<Array<TagInput>>;
  title: Scalars['String']['input'];
};

export type CreateUserInput = {
  password: Scalars['String']['input'];
  username: Scalars['String']['input'];
};

export type CurrentUser = {
  __typename?: 'CurrentUser';
  authenticated: Scalars['Boolean']['output'];
  authorities?: Maybe<Array<Scalars['String']['output']>>;
  id?: Maybe<Scalars['ID']['output']>;
};

export type DeleteUserInput = {
  id: Scalars['UUID']['input'];
};

export enum ErrorDetail {
  /**
   * The deadline expired before the operation could complete.
   *
   * For operations that change the state of the system, this error
   * may be returned even if the operation has completed successfully.
   * For example, a successful response from a server could have been
   * delayed long enough for the deadline to expire.
   *
   * HTTP Mapping: 504 Gateway Timeout
   * Error Type: UNAVAILABLE
   */
  DeadlineExceeded = 'DEADLINE_EXCEEDED',
  /**
   * The server detected that the client is exhibiting a behavior that
   * might be generating excessive load.
   *
   * HTTP Mapping: 429 Too Many Requests or 420 Enhance Your Calm
   * Error Type: UNAVAILABLE
   */
  EnhanceYourCalm = 'ENHANCE_YOUR_CALM',
  /**
   * The requested field is not found in the schema.
   *
   * This differs from `NOT_FOUND` in that `NOT_FOUND` should be used when a
   * query is valid, but is unable to return a result (if, for example, a
   * specific video id doesn't exist). `FIELD_NOT_FOUND` is intended to be
   * returned by the server to signify that the requested field is not known to exist.
   * This may be returned in lieu of failing the entire query.
   * See also `PERMISSION_DENIED` for cases where the
   * requested field is invalid only for the given user or class of users.
   *
   * HTTP Mapping: 404 Not Found
   * Error Type: BAD_REQUEST
   */
  FieldNotFound = 'FIELD_NOT_FOUND',
  /**
   * The client specified an invalid argument.
   *
   * Note that this differs from `FAILED_PRECONDITION`.
   * `INVALID_ARGUMENT` indicates arguments that are problematic
   * regardless of the state of the system (e.g., a malformed file name).
   *
   * HTTP Mapping: 400 Bad Request
   * Error Type: BAD_REQUEST
   */
  InvalidArgument = 'INVALID_ARGUMENT',
  /**
   * The provided cursor is not valid.
   *
   * The most common usage for this error is when a client is paginating
   * through a list that uses stateful cursors. In that case, the provided
   * cursor may be expired.
   *
   * HTTP Mapping: 404 Not Found
   * Error Type: NOT_FOUND
   */
  InvalidCursor = 'INVALID_CURSOR',
  /**
   * Unable to perform operation because a required resource is missing.
   *
   * Example: Client is attempting to refresh a list, but the specified
   * list is expired. This requires an action by the client to get a new list.
   *
   * If the user is simply trying GET a resource that is not found,
   * use the NOT_FOUND error type. FAILED_PRECONDITION.MISSING_RESOURCE
   * is to be used particularly when the user is performing an operation
   * that requires a particular resource to exist.
   *
   * HTTP Mapping: 400 Bad Request or 500 Internal Server Error
   * Error Type: FAILED_PRECONDITION
   */
  MissingResource = 'MISSING_RESOURCE',
  /**
   * Service Error.
   *
   * There is a problem with an upstream service.
   *
   * This may be returned if a gateway receives an unknown error from a service
   * or if a service is unreachable.
   * If a request times out which waiting on a response from a service,
   * `DEADLINE_EXCEEDED` may be returned instead.
   * If a service returns a more specific error Type, the specific error Type may
   * be returned instead.
   *
   * HTTP Mapping: 502 Bad Gateway
   * Error Type: UNAVAILABLE
   */
  ServiceError = 'SERVICE_ERROR',
  /**
   * Request failed due to network errors.
   *
   * HTTP Mapping: 503 Unavailable
   * Error Type: UNAVAILABLE
   */
  TcpFailure = 'TCP_FAILURE',
  /**
   * Request throttled based on server concurrency limits.
   *
   * HTTP Mapping: 503 Unavailable
   * Error Type: UNAVAILABLE
   */
  ThrottledConcurrency = 'THROTTLED_CONCURRENCY',
  /**
   * Request throttled based on server CPU limits
   *
   * HTTP Mapping: 503 Unavailable.
   * Error Type: UNAVAILABLE
   */
  ThrottledCpu = 'THROTTLED_CPU',
  /**
   * The operation is not implemented or is not currently supported/enabled.
   *
   * HTTP Mapping: 501 Not Implemented
   * Error Type: BAD_REQUEST
   */
  Unimplemented = 'UNIMPLEMENTED',
  /**
   * Unknown error.
   *
   * This error should only be returned when no other error detail applies.
   * If a client sees an unknown errorDetail, it will be interpreted as UNKNOWN.
   *
   * HTTP Mapping: 500 Internal Server Error
   */
  Unknown = 'UNKNOWN'
}

export enum ErrorType {
  /**
   * Bad Request.
   *
   * There is a problem with the request.
   * Retrying the same request is not likely to succeed.
   * An example would be a query or argument that cannot be deserialized.
   *
   * HTTP Mapping: 400 Bad Request
   */
  BadRequest = 'BAD_REQUEST',
  /**
   * The operation was rejected because the system is not in a state
   * required for the operation's execution.  For example, the directory
   * to be deleted is non-empty, an rmdir operation is applied to
   * a non-directory, etc.
   *
   * Service implementers can use the following guidelines to decide
   * between `FAILED_PRECONDITION` and `UNAVAILABLE`:
   *
   * - Use `UNAVAILABLE` if the client can retry just the failing call.
   * - Use `FAILED_PRECONDITION` if the client should not retry until
   * the system state has been explicitly fixed.  E.g., if an "rmdir"
   * fails because the directory is non-empty, `FAILED_PRECONDITION`
   * should be returned since the client should not retry unless
   * the files are deleted from the directory.
   *
   * HTTP Mapping: 400 Bad Request or 500 Internal Server Error
   */
  FailedPrecondition = 'FAILED_PRECONDITION',
  /**
   * Internal error.
   *
   * An unexpected internal error was encountered. This means that some
   * invariants expected by the underlying system have been broken.
   * This error code is reserved for serious errors.
   *
   * HTTP Mapping: 500 Internal Server Error
   */
  Internal = 'INTERNAL',
  /**
   * The requested entity was not found.
   *
   * This could apply to a resource that has never existed (e.g. bad resource id),
   * or a resource that no longer exists (e.g. cache expired.)
   *
   * Note to server developers: if a request is denied for an entire class
   * of users, such as gradual feature rollout or undocumented allowlist,
   * `NOT_FOUND` may be used. If a request is denied for some users within
   * a class of users, such as user-based access control, `PERMISSION_DENIED`
   * must be used.
   *
   * HTTP Mapping: 404 Not Found
   */
  NotFound = 'NOT_FOUND',
  /**
   * The caller does not have permission to execute the specified
   * operation.
   *
   * `PERMISSION_DENIED` must not be used for rejections
   * caused by exhausting some resource or quota.
   * `PERMISSION_DENIED` must not be used if the caller
   * cannot be identified (use `UNAUTHENTICATED`
   * instead for those errors).
   *
   * This error Type does not imply the
   * request is valid or the requested entity exists or satisfies
   * other pre-conditions.
   *
   * HTTP Mapping: 403 Forbidden
   */
  PermissionDenied = 'PERMISSION_DENIED',
  /**
   * The request does not have valid authentication credentials.
   *
   * This is intended to be returned only for routes that require
   * authentication.
   *
   * HTTP Mapping: 401 Unauthorized
   */
  Unauthenticated = 'UNAUTHENTICATED',
  /**
   * Currently Unavailable.
   *
   * The service is currently unavailable.  This is most likely a
   * transient condition, which can be corrected by retrying with
   * a backoff.
   *
   * HTTP Mapping: 503 Unavailable
   */
  Unavailable = 'UNAVAILABLE',
  /**
   * Unknown error.
   *
   * For example, this error may be returned when
   * an error code received from another address space belongs to
   * an error space that is not known in this address space.  Also
   * errors raised by APIs that do not return enough error information
   * may be converted to this error.
   *
   * If a client sees an unknown errorType, it will be interpreted as UNKNOWN.
   * Unknown errors MUST NOT trigger any special behavior. These MAY be treated
   * by an implementation as being equivalent to INTERNAL.
   *
   * When possible, a more specific error should be provided.
   *
   * HTTP Mapping: 520 Unknown Error
   */
  Unknown = 'UNKNOWN'
}

export type Mutation = {
  __typename?: 'Mutation';
  createComment: Comment;
  createPost: Post;
  createUser: User;
  deleteComment: Scalars['Boolean']['output'];
  deletePost: Scalars['Boolean']['output'];
  deleteUser: Scalars['Boolean']['output'];
  signIn?: Maybe<Scalars['Boolean']['output']>;
  signOut?: Maybe<Scalars['Boolean']['output']>;
  togglePostComments: Scalars['Boolean']['output'];
  togglePostLike: Scalars['Boolean']['output'];
  toggleUserComments: Scalars['Boolean']['output'];
  toggleUserPosts: Scalars['Boolean']['output'];
  updateComment: Comment;
  updatePost: Post;
  updateUser: User;
};


export type MutationCreateCommentArgs = {
  input: CreateCommentInput;
};


export type MutationCreatePostArgs = {
  input: CreatePostInput;
};


export type MutationCreateUserArgs = {
  input: CreateUserInput;
};


export type MutationDeleteCommentArgs = {
  id: Scalars['UUID']['input'];
};


export type MutationDeletePostArgs = {
  id: Scalars['UUID']['input'];
};


export type MutationDeleteUserArgs = {
  input: DeleteUserInput;
};


export type MutationSignInArgs = {
  input: SignInInput;
};


export type MutationTogglePostCommentsArgs = {
  postId: Scalars['UUID']['input'];
};


export type MutationTogglePostLikeArgs = {
  postId: Scalars['UUID']['input'];
};


export type MutationToggleUserCommentsArgs = {
  userId: Scalars['UUID']['input'];
};


export type MutationToggleUserPostsArgs = {
  userId: Scalars['UUID']['input'];
};


export type MutationUpdateCommentArgs = {
  input: UpdateCommentInput;
};


export type MutationUpdatePostArgs = {
  input: UpdatePostInput;
};


export type MutationUpdateUserArgs = {
  input: UpdateUserInput;
};

/**  ---------- Federated Types ---------- */
export type Post = {
  __typename?: 'Post';
  comments?: Maybe<Array<Comment>>;
  commentsDisabled?: Maybe<Scalars['Boolean']['output']>;
  commentsNumber?: Maybe<Scalars['Int']['output']>;
  content: Scalars['String']['output'];
  id: Scalars['UUID']['output'];
  tags?: Maybe<Array<Scalars['String']['output']>>;
  title: Scalars['String']['output'];
  userId?: Maybe<Scalars['UUID']['output']>;
};

export type PostPage = {
  __typename?: 'PostPage';
  data?: Maybe<Array<Post>>;
  hasNext?: Maybe<Scalars['Boolean']['output']>;
  hasPrev?: Maybe<Scalars['Boolean']['output']>;
  page?: Maybe<Scalars['Int']['output']>;
  size?: Maybe<Scalars['Int']['output']>;
};

/**  ---------- Input Types ---------- */
export type PostsLookupInput = {
  page?: Scalars['Int']['input'];
  size?: Scalars['Int']['input'];
  tags?: InputMaybe<Array<TagInput>>;
};

/**  ---------- Root Types (Query/Mutation/Subscription) ---------- */
export type Query = {
  __typename?: 'Query';
  comment?: Maybe<Comment>;
  me?: Maybe<CurrentUser>;
  post?: Maybe<Post>;
  posts?: Maybe<PostPage>;
  user?: Maybe<User>;
  users?: Maybe<UserPage>;
};


/**  ---------- Root Types (Query/Mutation/Subscription) ---------- */
export type QueryCommentArgs = {
  id: Scalars['UUID']['input'];
};


/**  ---------- Root Types (Query/Mutation/Subscription) ---------- */
export type QueryPostArgs = {
  id: Scalars['UUID']['input'];
};


/**  ---------- Root Types (Query/Mutation/Subscription) ---------- */
export type QueryPostsArgs = {
  input?: PostsLookupInput;
};


/**  ---------- Root Types (Query/Mutation/Subscription) ---------- */
export type QueryUserArgs = {
  id: Scalars['UUID']['input'];
};


/**  ---------- Root Types (Query/Mutation/Subscription) ---------- */
export type QueryUsersArgs = {
  input?: UsersLookupInput;
};

export type SignInInput = {
  password: Scalars['String']['input'];
  username: Scalars['String']['input'];
};

export type TagInput = {
  name: Scalars['String']['input'];
};

export type UpdateCommentInput = {
  content?: InputMaybe<Scalars['String']['input']>;
  id: Scalars['UUID']['input'];
};

export type UpdatePostInput = {
  content?: InputMaybe<Scalars['String']['input']>;
  id: Scalars['UUID']['input'];
  tags?: InputMaybe<Array<TagInput>>;
  title?: InputMaybe<Scalars['String']['input']>;
};

export type UpdateUserInput = {
  id: Scalars['UUID']['input'];
  password?: InputMaybe<Scalars['String']['input']>;
};

/**  ---------- Federated Types ---------- */
export type User = {
  __typename?: 'User';
  authorities?: Maybe<Array<Scalars['String']['output']>>;
  comments?: Maybe<Array<Comment>>;
  commentsDisabled?: Maybe<Scalars['Boolean']['output']>;
  id: Scalars['UUID']['output'];
  posts?: Maybe<Array<Post>>;
  postsDisabled?: Maybe<Scalars['Boolean']['output']>;
  username?: Maybe<Scalars['String']['output']>;
};

export type UserPage = {
  __typename?: 'UserPage';
  data?: Maybe<Array<User>>;
  hasNext?: Maybe<Scalars['Boolean']['output']>;
  hasPrev?: Maybe<Scalars['Boolean']['output']>;
  page?: Maybe<Scalars['Int']['output']>;
  size?: Maybe<Scalars['Int']['output']>;
};

/**  ---------- Input Types ---------- */
export type UsersLookupInput = {
  page?: Scalars['Int']['input'];
  size?: Scalars['Int']['input'];
};

export type CreatePostMutationVariables = Exact<{
  input: CreatePostInput;
}>;


export type CreatePostMutation = { __typename?: 'Mutation', createPost: { __typename?: 'Post', id: string, userId?: string | null, title: string, content: string, tags?: Array<string> | null } };

export type PostFieldsFragment = { __typename?: 'Post', id: string } & { ' $fragmentName'?: 'PostFieldsFragment' };

export type CreateUserMutationVariables = Exact<{
  input: CreateUserInput;
}>;


export type CreateUserMutation = { __typename?: 'Mutation', createUser: { __typename?: 'User', id: string, username?: string | null } };

export type NewUserFragment = { __typename?: 'User', id: string, username?: string | null } & { ' $fragmentName'?: 'NewUserFragment' };

export type GetCurrentUserInfoQueryVariables = Exact<{ [key: string]: never; }>;


export type GetCurrentUserInfoQuery = { __typename?: 'Query', currentUser?: { __typename?: 'CurrentUser', id?: string | null, authorities?: Array<string> | null, authenticated: boolean } | null };

export type GetPostsPageQueryVariables = Exact<{
  page: Scalars['Int']['input'];
  size: Scalars['Int']['input'];
}>;


export type GetPostsPageQuery = { __typename?: 'Query', posts?: { __typename?: 'PostPage', data?: Array<{ __typename?: 'Post', id: string, title: string, tags?: Array<string> | null }> | null } | null };

export type SignInMutationVariables = Exact<{
  input: SignInInput;
}>;


export type SignInMutation = { __typename?: 'Mutation', signIn?: boolean | null };

export type SignOutMutationVariables = Exact<{ [key: string]: never; }>;


export type SignOutMutation = { __typename?: 'Mutation', signOut?: boolean | null };

export const PostFieldsFragmentDoc = {"kind":"Document","definitions":[{"kind":"FragmentDefinition","name":{"kind":"Name","value":"postFields"},"typeCondition":{"kind":"NamedType","name":{"kind":"Name","value":"Post"}},"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"id"}}]}}]} as unknown as DocumentNode<PostFieldsFragment, unknown>;
export const NewUserFragmentDoc = {"kind":"Document","definitions":[{"kind":"FragmentDefinition","name":{"kind":"Name","value":"NewUser"},"typeCondition":{"kind":"NamedType","name":{"kind":"Name","value":"User"}},"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"id"}},{"kind":"Field","name":{"kind":"Name","value":"username"}}]}}]} as unknown as DocumentNode<NewUserFragment, unknown>;
export const CreatePostDocument = {"kind":"Document","definitions":[{"kind":"OperationDefinition","operation":"mutation","name":{"kind":"Name","value":"CreatePost"},"variableDefinitions":[{"kind":"VariableDefinition","variable":{"kind":"Variable","name":{"kind":"Name","value":"input"}},"type":{"kind":"NonNullType","type":{"kind":"NamedType","name":{"kind":"Name","value":"CreatePostInput"}}}}],"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"createPost"},"arguments":[{"kind":"Argument","name":{"kind":"Name","value":"input"},"value":{"kind":"Variable","name":{"kind":"Name","value":"input"}}}],"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"id"}},{"kind":"Field","name":{"kind":"Name","value":"userId"}},{"kind":"Field","name":{"kind":"Name","value":"title"}},{"kind":"Field","name":{"kind":"Name","value":"content"}},{"kind":"Field","name":{"kind":"Name","value":"tags"}}]}}]}}]} as unknown as DocumentNode<CreatePostMutation, CreatePostMutationVariables>;
export const CreateUserDocument = {"kind":"Document","definitions":[{"kind":"OperationDefinition","operation":"mutation","name":{"kind":"Name","value":"CreateUser"},"variableDefinitions":[{"kind":"VariableDefinition","variable":{"kind":"Variable","name":{"kind":"Name","value":"input"}},"type":{"kind":"NonNullType","type":{"kind":"NamedType","name":{"kind":"Name","value":"CreateUserInput"}}}}],"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"createUser"},"arguments":[{"kind":"Argument","name":{"kind":"Name","value":"input"},"value":{"kind":"Variable","name":{"kind":"Name","value":"input"}}}],"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"id"}},{"kind":"Field","name":{"kind":"Name","value":"username"}}]}}]}}]} as unknown as DocumentNode<CreateUserMutation, CreateUserMutationVariables>;
export const GetCurrentUserInfoDocument = {"kind":"Document","definitions":[{"kind":"OperationDefinition","operation":"query","name":{"kind":"Name","value":"GetCurrentUserInfo"},"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","alias":{"kind":"Name","value":"currentUser"},"name":{"kind":"Name","value":"me"},"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"id"}},{"kind":"Field","name":{"kind":"Name","value":"authorities"}},{"kind":"Field","name":{"kind":"Name","value":"authenticated"}}]}}]}}]} as unknown as DocumentNode<GetCurrentUserInfoQuery, GetCurrentUserInfoQueryVariables>;
export const GetPostsPageDocument = {"kind":"Document","definitions":[{"kind":"OperationDefinition","operation":"query","name":{"kind":"Name","value":"GetPostsPage"},"variableDefinitions":[{"kind":"VariableDefinition","variable":{"kind":"Variable","name":{"kind":"Name","value":"page"}},"type":{"kind":"NonNullType","type":{"kind":"NamedType","name":{"kind":"Name","value":"Int"}}}},{"kind":"VariableDefinition","variable":{"kind":"Variable","name":{"kind":"Name","value":"size"}},"type":{"kind":"NonNullType","type":{"kind":"NamedType","name":{"kind":"Name","value":"Int"}}}}],"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"posts"},"arguments":[{"kind":"Argument","name":{"kind":"Name","value":"input"},"value":{"kind":"ObjectValue","fields":[{"kind":"ObjectField","name":{"kind":"Name","value":"page"},"value":{"kind":"Variable","name":{"kind":"Name","value":"page"}}},{"kind":"ObjectField","name":{"kind":"Name","value":"size"},"value":{"kind":"Variable","name":{"kind":"Name","value":"size"}}}]}}],"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"data"},"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"id"}},{"kind":"Field","name":{"kind":"Name","value":"title"}},{"kind":"Field","name":{"kind":"Name","value":"tags"}}]}}]}}]}}]} as unknown as DocumentNode<GetPostsPageQuery, GetPostsPageQueryVariables>;
export const SignInDocument = {"kind":"Document","definitions":[{"kind":"OperationDefinition","operation":"mutation","name":{"kind":"Name","value":"SignIn"},"variableDefinitions":[{"kind":"VariableDefinition","variable":{"kind":"Variable","name":{"kind":"Name","value":"input"}},"type":{"kind":"NonNullType","type":{"kind":"NamedType","name":{"kind":"Name","value":"SignInInput"}}}}],"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"signIn"},"arguments":[{"kind":"Argument","name":{"kind":"Name","value":"input"},"value":{"kind":"Variable","name":{"kind":"Name","value":"input"}}}]}]}}]} as unknown as DocumentNode<SignInMutation, SignInMutationVariables>;
export const SignOutDocument = {"kind":"Document","definitions":[{"kind":"OperationDefinition","operation":"mutation","name":{"kind":"Name","value":"SignOut"},"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"signOut"}}]}}]} as unknown as DocumentNode<SignOutMutation, SignOutMutationVariables>;
