import { Navigate } from 'react-router';

import { useGetCurrentUserQuery } from '@/api/users-api';
import Loader from '@/components/Loader';
import SignUpForm from '@/components/SignUpForm';

export default function SignUp() {
    const { isLoading, data } = useGetCurrentUserQuery(undefined, {
        refetchOnMountOrArgChange: true,
    });

    if (isLoading) {
        return <Loader />;
    }
    return data?.currentUser?.authenticated
        ? <Navigate to='/' />
        : <SignUpForm />;
}
