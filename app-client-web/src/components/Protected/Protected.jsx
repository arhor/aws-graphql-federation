import PropTypes from 'prop-types';

import { withProtection } from '@/components/Protected/withProtection';

Protected.propTypes = {
    component: PropTypes.elementType.isRequired,
    props: PropTypes.any,
};

export default function Protected({
    component,
    props,
}) {
    const ProtectedComponent = withProtection(component);
    return <ProtectedComponent {...props} />;
}
