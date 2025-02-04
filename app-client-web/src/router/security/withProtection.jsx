import { Navigate } from 'react-router';

import { useGetCurrentUserQuery } from '@/api/users-api';
import Loader from '@/components/Loader';

function determineNameOf(Component) {
    return Component.displayName
        || Component.name
        || 'AnonymousComponent';
}
function authorized(currentUser, authorities) {
    return currentUser
        && currentUser.authenticated
        && authorities.every(auth => currentUser.authorities.includes(auth));
}

export function withProtection(Component, authorities) {
    const ProtectedComponent = (props) => {
        const { isLoading, data } = useGetCurrentUserQuery();

        if (isLoading) {
            return <Loader />;
        }
        return authorized(data?.currentUser, authorities)
            ? <Component {...props} />
            : <Navigate to="/sign-in" />;
    };
    ProtectedComponent.displayName = `withProtection(${determineNameOf(Component)})`;
    return ProtectedComponent;
}
