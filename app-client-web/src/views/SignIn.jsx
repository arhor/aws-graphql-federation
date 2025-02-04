import { Navigate } from 'react-router';

import { useGetCurrentUserQuery } from '@/api/users-api';
import Loader from '@/components/Loader';
import SignInForm from '@/components/SignInForm';

export default function SignIn() {
    const { isLoading, data } = useGetCurrentUserQuery(undefined, {
        refetchOnMountOrArgChange: true,
    });

    if (isLoading) {
        return <Loader />;
    }
    return data?.currentUser?.authenticated
        ? <Navigate to='/' />
        : <SignInForm />;
}
