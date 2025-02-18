import { createBrowserRouter, RouterProvider } from 'react-router';

import Layout from '@/components/Layout';
import Protected from '@/router/security/Protected';
import NotFound from '@/views/NotFound';
import SignIn from '@/views/SignIn';
import SignUp from '@/views/SignUp';

function unwrap(module) {
    return { Component: module.default };
}

const router = createBrowserRouter([
    {
        element: <Protected component={Layout} />,
        children: [
            {
                index: true,
                lazy: () => import('@/views/Home').then(unwrap),
            }
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
