import { useEffect } from 'react';
import { Navigate, useLocation } from 'react-router';

import { SignUpForm } from '@/components';
import { useStore } from '@/store';

export default function SignUp() {
    const { state } = useLocation();
    const { user } = useStore();

    useEffect(() => {
        if (!state?.doNotCallAuth) {
            user.fetchData();
        }
    }, [state, user]);

    return (!state?.doNotCallAuth || user.authenticated)
        ? <Navigate to="/" />
        : <SignUpForm />;
}
