import { createBrowserRouter, RouterProvider } from 'react-router';

import Layout from '@/components/Layout';
import UserSettings from '@/components/UserSettings';
import NotFound from '@/views/NotFound';
import SignIn from '@/views/SignIn';
import SignUp from '@/views/SignUp';

function unwrap(module) {
    return { Component: module.default };
}

const router = createBrowserRouter([
    {
        element: <Layout/>,
        children: [
            {
                index: true,
                lazy: () => import('@/views/Home').then(unwrap),
            },
            {
                path: '/settings',
                element: <UserSettings />,
            },
            {
                path: '/sign-in',
                element: <SignIn />,
            },
            {
                path: '/sign-up',
                element: <SignUp />,
            },
            {
                path: '*',
                element: <NotFound />,
            },
        ],
    },
]);

export default function AppRouter() {
    return <RouterProvider router={router} />;
}

if (import.meta.hot) {
    import.meta.hot.dispose(() => router.dispose());
}
