import { createElement, Suspense } from 'react';
import { isElement, isLazy } from 'react-is';
import { Navigate } from 'react-router';

import Loading from '@/components/Loading';
import useCurrentUser from '@/hooks/useCurrentUser';

function determineNameOf(Component) {
    return Component.displayName
        || Component.name
        || 'Component';
}

export function withProtection(Component, authorities = []) {
    const ProtectedComponent = (props) => {
        const { loading, data } = useCurrentUser();

        if (loading) {
            return <Loading />;
        }
        if (data?.currentUser) {
            if (authorities.length > 0) {
                data.currentUser.authorities.forEach((authority) => {
                    if (!authorities.includes(authority)) {
                        return <Navigate to="/sign-in" />;
                    }
                });
            }
            return <Component {...props} />;
        }
        return <Navigate to="/sign-in" />;
    };
    ProtectedComponent.displayName = `withProtection(${determineNameOf(Component)})`;
    return ProtectedComponent;
}