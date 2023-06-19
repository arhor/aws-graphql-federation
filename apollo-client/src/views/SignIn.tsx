import { Navigate, useSearchParams } from 'react-router-dom';

import Loading from '@/components/Loading/Loading';
import SignInForm from '@/components/SignInForm';
import useCurrentUser from '@/hooks/useCurrentUser';

const SignIn = () => {
    const [ searchParams ] = useSearchParams();
    const { loading, data } = useCurrentUser();

    if (searchParams.has('auth') && searchParams.get('auth') == 'success') {
        return (
            <Navigate to="/" />
        );
    } else {
        if (loading) {
            return (
                <Loading />
            );
        }
        return data?.currentUser ? (
            <Navigate to="/" />
        ) : (
            <SignInForm />
        );
    }
};

export default SignIn;
