/* eslint-disable */
import { TypedDocumentNode as DocumentNode } from '@graphql-typed-document-node/core';
export type Maybe<T> = T | null;
export type InputMaybe<T> = Maybe<T>;
export type Exact<T extends { [key: string]: unknown }> = { [K in keyof T]: T[K] };
export type MakeOptional<T, K extends keyof T> = Omit<T, K> & { [SubKey in K]?: Maybe<T[SubKey]> };
export type MakeMaybe<T, K extends keyof T> = Omit<T, K> & { [SubKey in K]: Maybe<T[SubKey]> };
/** All built-in and custom scalars, mapped to their actual values */
export type Scalars = {
  ID: string;
  String: string;
  Boolean: boolean;
  Int: number;
  Float: number;
  Long: number;
  Settings: string[];
};

export type CreateExtraDataRequest = {
  entityId: Scalars['Long'];
  entityType: EntityType;
  propertyName: Scalars['String'];
  propertyValue?: InputMaybe<Scalars['String']>;
};

export type CreatePostRequest = {
  content: Scalars['String'];
  topicId: Scalars['Long'];
  userId: Scalars['Long'];
};

export type CreateTopicRequest = {
  name: Scalars['String'];
  userId: Scalars['Long'];
};

export type CreateUserRequest = {
  password: Scalars['String'];
  settings?: InputMaybe<Scalars['Settings']>;
  username: Scalars['String'];
};

export enum EntityType {
  Post = 'Post',
  User = 'User'
}

export type ExtraData = {
  __typename?: 'ExtraData';
  entityId: Scalars['Long'];
  entityType: Scalars['String'];
  id: Scalars['ID'];
  propertyName: Scalars['String'];
  propertyValue?: Maybe<Scalars['String']>;
};

export type Mutation = {
  __typename?: 'Mutation';
  createExtraData?: Maybe<ExtraData>;
  createPost: Post;
  createTopic: Topic;
  createUser: User;
};


export type MutationCreateExtraDataArgs = {
  request: CreateExtraDataRequest;
};


export type MutationCreatePostArgs = {
  request: CreatePostRequest;
};


export type MutationCreateTopicArgs = {
  request: CreateTopicRequest;
};


export type MutationCreateUserArgs = {
  request: CreateUserRequest;
};

export type Post = {
  __typename?: 'Post';
  content: Scalars['String'];
  extraData?: Maybe<Array<Maybe<ExtraData>>>;
  id: Scalars['Long'];
  topicId: Scalars['Long'];
  userId?: Maybe<Scalars['Long']>;
};


export type PostExtraDataArgs = {
  properties?: InputMaybe<Array<Scalars['String']>>;
};

export type Query = {
  __typename?: 'Query';
  availableUserSettings: Scalars['Settings'];
  currentUser?: Maybe<User>;
  topics: Array<Topic>;
  user?: Maybe<User>;
  users: Array<User>;
};


export type QueryUserArgs = {
  username?: InputMaybe<Scalars['String']>;
};

export type Topic = {
  __typename?: 'Topic';
  id: Scalars['Long'];
  name: Scalars['String'];
  posts?: Maybe<Array<Post>>;
  userId?: Maybe<Scalars['Long']>;
};

export type User = {
  __typename?: 'User';
  extraData?: Maybe<Array<Maybe<ExtraData>>>;
  id: Scalars['Long'];
  posts?: Maybe<Array<Post>>;
  settings?: Maybe<Scalars['Settings']>;
  username: Scalars['String'];
};


export type UserExtraDataArgs = {
  properties?: InputMaybe<Array<Scalars['String']>>;
};

export type GetAvailableUserSettingsQueryVariables = Exact<{ [key: string]: never; }>;


export type GetAvailableUserSettingsQuery = { __typename?: 'Query', availableUserSettings: string[] };

export type CreateTopicMutationVariables = Exact<{
  name: Scalars['String'];
  userId: Scalars['Long'];
}>;


export type CreateTopicMutation = { __typename?: 'Mutation', createTopic: { __typename?: 'Topic', id: number, userId?: number | null, name: string } };

export type NewTopicFragment = { __typename?: 'Topic', id: number, userId?: number | null, name: string } & { ' $fragmentName'?: 'NewTopicFragment' };

export type CreateUserMutationVariables = Exact<{
  username: Scalars['String'];
  password: Scalars['String'];
  settings?: InputMaybe<Scalars['Settings']>;
}>;


export type CreateUserMutation = { __typename?: 'Mutation', createUser: { __typename?: 'User', id: number, username: string, settings?: string[] | null } };

export type NewUserFragment = { __typename?: 'User', id: number, username: string, settings?: string[] | null } & { ' $fragmentName'?: 'NewUserFragment' };

export type GetCurrentUserQueryVariables = Exact<{ [key: string]: never; }>;


export type GetCurrentUserQuery = { __typename?: 'Query', currentUser?: { __typename?: 'User', id: number, username: string, settings?: string[] | null } | null };

export type GetTopicsQueryVariables = Exact<{ [key: string]: never; }>;


export type GetTopicsQuery = { __typename?: 'Query', topics: Array<{ __typename?: 'Topic', id: number, name: string, posts?: Array<{ __typename?: 'Post', id: number, content: string }> | null }> };

export const NewTopicFragmentDoc = {"kind":"Document","definitions":[{"kind":"FragmentDefinition","name":{"kind":"Name","value":"NewTopic"},"typeCondition":{"kind":"NamedType","name":{"kind":"Name","value":"Topic"}},"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"id"}},{"kind":"Field","name":{"kind":"Name","value":"userId"}},{"kind":"Field","name":{"kind":"Name","value":"name"}}]}}]} as unknown as DocumentNode<NewTopicFragment, unknown>;
export const NewUserFragmentDoc = {"kind":"Document","definitions":[{"kind":"FragmentDefinition","name":{"kind":"Name","value":"NewUser"},"typeCondition":{"kind":"NamedType","name":{"kind":"Name","value":"User"}},"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"id"}},{"kind":"Field","name":{"kind":"Name","value":"username"}},{"kind":"Field","name":{"kind":"Name","value":"settings"}}]}}]} as unknown as DocumentNode<NewUserFragment, unknown>;
export const GetAvailableUserSettingsDocument = {"kind":"Document","definitions":[{"kind":"OperationDefinition","operation":"query","name":{"kind":"Name","value":"GetAvailableUserSettings"},"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"availableUserSettings"}}]}}]} as unknown as DocumentNode<GetAvailableUserSettingsQuery, GetAvailableUserSettingsQueryVariables>;
export const CreateTopicDocument = {"kind":"Document","definitions":[{"kind":"OperationDefinition","operation":"mutation","name":{"kind":"Name","value":"CreateTopic"},"variableDefinitions":[{"kind":"VariableDefinition","variable":{"kind":"Variable","name":{"kind":"Name","value":"name"}},"type":{"kind":"NonNullType","type":{"kind":"NamedType","name":{"kind":"Name","value":"String"}}}},{"kind":"VariableDefinition","variable":{"kind":"Variable","name":{"kind":"Name","value":"userId"}},"type":{"kind":"NonNullType","type":{"kind":"NamedType","name":{"kind":"Name","value":"Long"}}}}],"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"createTopic"},"arguments":[{"kind":"Argument","name":{"kind":"Name","value":"request"},"value":{"kind":"ObjectValue","fields":[{"kind":"ObjectField","name":{"kind":"Name","value":"name"},"value":{"kind":"Variable","name":{"kind":"Name","value":"name"}}},{"kind":"ObjectField","name":{"kind":"Name","value":"userId"},"value":{"kind":"Variable","name":{"kind":"Name","value":"userId"}}}]}}],"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"id"}},{"kind":"Field","name":{"kind":"Name","value":"userId"}},{"kind":"Field","name":{"kind":"Name","value":"name"}}]}}]}}]} as unknown as DocumentNode<CreateTopicMutation, CreateTopicMutationVariables>;
export const CreateUserDocument = {"kind":"Document","definitions":[{"kind":"OperationDefinition","operation":"mutation","name":{"kind":"Name","value":"CreateUser"},"variableDefinitions":[{"kind":"VariableDefinition","variable":{"kind":"Variable","name":{"kind":"Name","value":"username"}},"type":{"kind":"NonNullType","type":{"kind":"NamedType","name":{"kind":"Name","value":"String"}}}},{"kind":"VariableDefinition","variable":{"kind":"Variable","name":{"kind":"Name","value":"password"}},"type":{"kind":"NonNullType","type":{"kind":"NamedType","name":{"kind":"Name","value":"String"}}}},{"kind":"VariableDefinition","variable":{"kind":"Variable","name":{"kind":"Name","value":"settings"}},"type":{"kind":"NamedType","name":{"kind":"Name","value":"Settings"}}}],"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"createUser"},"arguments":[{"kind":"Argument","name":{"kind":"Name","value":"request"},"value":{"kind":"ObjectValue","fields":[{"kind":"ObjectField","name":{"kind":"Name","value":"username"},"value":{"kind":"Variable","name":{"kind":"Name","value":"username"}}},{"kind":"ObjectField","name":{"kind":"Name","value":"password"},"value":{"kind":"Variable","name":{"kind":"Name","value":"password"}}},{"kind":"ObjectField","name":{"kind":"Name","value":"settings"},"value":{"kind":"Variable","name":{"kind":"Name","value":"settings"}}}]}}],"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"id"}},{"kind":"Field","name":{"kind":"Name","value":"username"}},{"kind":"Field","name":{"kind":"Name","value":"settings"}}]}}]}}]} as unknown as DocumentNode<CreateUserMutation, CreateUserMutationVariables>;
export const GetCurrentUserDocument = {"kind":"Document","definitions":[{"kind":"OperationDefinition","operation":"query","name":{"kind":"Name","value":"GetCurrentUser"},"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"currentUser"},"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"id"}},{"kind":"Field","name":{"kind":"Name","value":"username"}},{"kind":"Field","name":{"kind":"Name","value":"settings"}}]}}]}}]} as unknown as DocumentNode<GetCurrentUserQuery, GetCurrentUserQueryVariables>;
export const GetTopicsDocument = {"kind":"Document","definitions":[{"kind":"OperationDefinition","operation":"query","name":{"kind":"Name","value":"GetTopics"},"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"topics"},"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"id"}},{"kind":"Field","name":{"kind":"Name","value":"name"}},{"kind":"Field","name":{"kind":"Name","value":"posts"},"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"id"}},{"kind":"Field","name":{"kind":"Name","value":"content"}}]}}]}}]}}]} as unknown as DocumentNode<GetTopicsQuery, GetTopicsQueryVariables>;