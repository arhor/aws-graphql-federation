import { Navigate } from 'react-router-dom';

import Loading from '@/components/Loading';
import SignUpForm from '@/components/SignUpForm';
import useCurrentUser from '@/hooks/useCurrentUser';

const SignUp = () => {
    const { loading, data } = useCurrentUser();

    if (loading) {
        return (
            <Loading />
        );
    }

    return data?.currentUser ? (
        <Navigate to="/" />
    ) : (
        <SignUpForm />
    );
};

export default SignUp;
