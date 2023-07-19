import { createBrowserRouter } from 'react-router-dom';

import Layout from '@/components/Layout';
import Home from '@/views/Home';
import NotFound from '@/views/NotFound';
import SignIn from '@/views/SignIn';
import SignUp from '@/views/SignUp';

export const router = createBrowserRouter([
    {
        path: '',
        element: <Layout />,
        children: [
            {
                index: true,
                element: <Home />,
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

if (import.meta.hot) {
    import.meta.hot.dispose(() => router.dispose());
}
