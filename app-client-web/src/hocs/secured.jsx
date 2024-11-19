import { Suspense } from 'react';

import { Navigate } from 'react-router';

import Loading from '@/components/Loading';
import useCurrentUser from '@/hooks/useCurrentUser';

const REACT_LAZY_TYPE = Symbol.for('react.lazy');

/**
 * @template T
 * @param {React.ComponentType<T> & { $$typeof?: symbol | number | null }} WrappedComponent
 * @returns {React.ComponentType<T>}
 */
export default function secured(WrappedComponent) {
    /**
     * @param {T} props
     * @returns {React.ReactNode}
     */
    const SecuredComponent = (props) => {
        const { loading, data } = useCurrentUser();

        if (loading) {
            return <Loading />;
        }

        if (data?.currentUser) {
            if (WrappedComponent.$$typeof === REACT_LAZY_TYPE) {
                return (
                    <Suspense fallback={<Loading />}>
                        <WrappedComponent {...props} />
                    </Suspense>
                );
            } else {
                return <WrappedComponent {...props} />;
            }
        } else {
            return <Navigate to="/sign-in" />;
        }
    };
    SecuredComponent.displayName = `secured(${
        WrappedComponent.displayName || WrappedComponent.name || 'Component'
    })`;
    return SecuredComponent;
}
