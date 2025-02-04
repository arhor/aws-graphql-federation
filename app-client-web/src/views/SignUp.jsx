import { Navigate } from 'react-router';

import { useGetCurrentUserQuery } from '@/api/users-api';
import SignUpForm from '@/components/SignUpForm';

export default function SignUp() {
    const { data } = useGetCurrentUserQuery();

    return data?.currentUser?.authenticated
        ? <Navigate to='/' />
        : <SignUpForm />;
}
