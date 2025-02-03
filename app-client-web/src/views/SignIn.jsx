import { useEffect } from 'react';
import { Navigate, useSearchParams } from 'react-router';

import { SignInForm } from '@/components';
import { useStore } from '@/store';

export default function SignIn() {
    const [searchParams] = useSearchParams();
    const { user } = useStore();

    useEffect(() => {
        user.fetchData();
    }, [user]);

    return (searchParams.get('auth') == 'success' || user.authenticated)
        ? <Navigate to="/" />
        : <SignInForm />;
}
