import { createBrowserRouter } from 'react-router-dom';

import AppLayout from '@/components/AppLayout';
import Home from '@/views/Home.tsx';
import NotFound from '@/views/NotFound.tsx';
import SignIn from '@/views/SignIn.tsx';
import SignUp from '@/views/SignUp.tsx';

export const router = createBrowserRouter([
    {
        path: '',
        element: <AppLayout />,
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
