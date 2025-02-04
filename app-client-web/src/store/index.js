import { configureStore } from '@reduxjs/toolkit';

import authApi from '@/api/auth-api';
import postsApi from '@/api/posts-api';
import usersApi from '@/api/users-api';

const store = configureStore({
    reducer: {
        [authApi.reducerPath]: authApi.reducer,
        [postsApi.reducerPath]: postsApi.reducer,
        [usersApi.reducerPath]: usersApi.reducer,
    },
    middleware: (getDefaultMiddleware) => getDefaultMiddleware()
        .concat(authApi.middleware)
        .concat(postsApi.middleware)
        .concat(usersApi.middleware)
});

export default store;
