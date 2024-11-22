import { Navigate } from 'react-router';

import Loading from '@/components/Loading';
import useCurrentUser from '@/hooks/useCurrentUser';

function determineNameOf(Component) {
    return Component.displayName
        || Component.name
        || 'Component';
}
function authorized(currentUser, authorities) {
    return currentUser
        && currentUser.authenticated
        && authorities.every(auth => currentUser.authorities.includes(auth));
}

export function withProtection(Component, authorities) {
    const ProtectedComponent = (props) => {
        const { loading, data } = useCurrentUser();

        if (loading) {
            return <Loading />;
        }
        return authorized(data?.currentUser, authorities)
            ? <Component {...props} />
            : <Navigate to="/sign-in" />;
    };
    ProtectedComponent.displayName = `withProtection(${determineNameOf(Component)})`;
    return ProtectedComponent;
}
