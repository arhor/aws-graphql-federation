import { ComponentType, Suspense } from 'react';

import { Navigate } from 'react-router';

import Loading from '@/components/Loading';
import useCurrentUser from '@/hooks/useCurrentUser';
import { Optional } from '@/utils/core-utils';

const REACT_LAZY_TYPE = Symbol.for('react.lazy');

export default function secured<T extends JSX.IntrinsicAttributes>(
    WrappedComponent: ComponentType<T> & { $$typeof: Optional<symbol | number> },
): ComponentType<T> {
    const SecuredComponent = (props: T) => {
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
