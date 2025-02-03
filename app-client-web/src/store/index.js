import { configureStore } from '@reduxjs/toolkit';

import postsApi from '@/api/posts-api';
import usersApi from '@/api/users-api';

export const store = configureStore({
    reducer: {
        [postsApi.reducerPath]: postsApi.reducer,
        [usersApi.reducerPath]: usersApi.reducer,
    },
    middleware: (getDefaultMiddleware) => getDefaultMiddleware()
        .concat(postsApi.middleware)
        .concat(usersApi.middleware)
});
