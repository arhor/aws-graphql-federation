import { lazy } from 'react';
import { createBrowserRouter, RouterProvider } from 'react-router';

import Layout from '@/components/Layout';
import Protected from '@/router/security/Protected';
import NotFound from '@/views/NotFound';
import SignIn from '@/views/SignIn';
import SignUp from '@/views/SignUp';

const router = createBrowserRouter([
    {
        element: <Protected component={Layout} />,
        children: [
            {
                index: true,
                Component: lazy(() => import('@/views/Home')),
            },
            {
                path: '*',
                Component: NotFound,
            },
        ],
    },
    {
        path: '/sign-in',
        Component: SignIn,
    },
    {
        path: '/sign-up',
        Component: SignUp,
    },
    {
        path: '*',
        Component: NotFound,
    },
]);

export default function AppRouter() {
    return <RouterProvider router={router} />;
}

if (import.meta.hot) {
    import.meta.hot.dispose(() => router.dispose());
}
