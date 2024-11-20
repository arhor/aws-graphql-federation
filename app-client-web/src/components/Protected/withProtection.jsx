import { Suspense } from 'react';
import { Navigate } from 'react-router';

import Loading from '@/components/Loading';
import useCurrentUser from '@/hooks/useCurrentUser';

const REACT_LAZY_TYPE = Symbol.for('react.lazy');

/**
 * @template T
 * @param {React.ComponentType<T> & { $$typeof?: symbol | number | null }} Component
 * @returns {React.ComponentType<T>}
 */
export function withProtection(Component) {
    /**
     * @param {T} props
     * @returns {React.ReactNode}
     */
    const ProtectedComponent = (props) => {
        const { loading, data } = useCurrentUser();

        if (loading) {
            return <Loading />;
        }
        if (data?.currentUser) {
            if (Component.$$typeof === REACT_LAZY_TYPE) {
                return (
                    <Suspense fallback={<Loading />}>
                        <Component {...props} />
                    </Suspense>
                );
            } else {
                return <Component {...props} />;
            }
        } else {
            return <Navigate to="/sign-in" />;
        }
    };
    ProtectedComponent.displayName = `withProtection(${
        Component.displayName || Component.name || 'Component'
    })`;
    return ProtectedComponent;
}