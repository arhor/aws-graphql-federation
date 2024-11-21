import { Suspense } from 'react';
import { isLazy } from 'react-is';
import { Navigate } from 'react-router';

import Loading from '@/components/Loading';
import useCurrentUser from '@/hooks/useCurrentUser';

function determineNameOf(Component) {
    return Component.displayName
        || Component.name
        || 'Component';
}

export function withProtection(Component) {
    const ProtectedComponent = (props) => {
        const { loading, data } = useCurrentUser();

        if (loading) {
            return <Loading />;
        }
        if (data?.currentUser) {
            return isLazy(Component) ? (
                <Suspense fallback={<Loading />}>
                    <Component {...props} />
                </Suspense>
            ) : (
                <Component {...props} />
            );
        }
        return <Navigate to="/sign-in" />;
    };
    ProtectedComponent.displayName = `withProtection(${determineNameOf(Component)})`;
    return ProtectedComponent;
}