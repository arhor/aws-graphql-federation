import { Navigate } from 'react-router';

import { useGetCurrentUserQuery } from '@/api/users-api';
import SignInForm from '@/components/SignInForm';

export default function SignIn() {
    const { data } = useGetCurrentUserQuery();

    return data?.currentUser?.authenticated
        ? <Navigate to='/' />
        : <SignInForm />;
}
