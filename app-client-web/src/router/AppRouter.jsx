import { createBrowserRouter, RouterProvider } from 'react-router-dom';

import { Layout } from '@/components';
import { NotFound, SignIn, SignUp } from '@/views';
import Protected from '@/router/security/Protected';

const router = createBrowserRouter([
    {
        path: '',
        element: <Protected component={Layout} />,
        children: [
            {
                index: true,
                lazy: () => import('@/views/Home'),
            }
        ],
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
]);

export default function AppRouter() {
    return <RouterProvider router={router} />;
}

if (import.meta.hot) {
    import.meta.hot.dispose(() => router.dispose());
}
