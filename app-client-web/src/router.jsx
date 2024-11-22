import { createBrowserRouter } from 'react-router-dom';

import Layout from '@/components/Layout';
import Protected from '@/components/Protected';
import Feed from '@/views/Feed';
import NotFound from '@/views/NotFound';
import SignIn from '@/views/SignIn';
import SignUp from '@/views/SignUp';

export const router = createBrowserRouter([
    {
        path: '',
        element: <Protected component={Layout} />,
        children: [
            {
                index: true,
                element: <Feed />,
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
