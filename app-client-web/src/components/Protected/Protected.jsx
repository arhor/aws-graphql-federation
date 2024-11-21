import PropTypes from 'prop-types';

import { withProtection } from '@/components/Protected/withProtection';

export default function Protected({ component, ...rest }) {
    const ProtectedComponent = withProtection(component);
    return <ProtectedComponent {...rest} />;
}

Protected.propTypes = {
    component: PropTypes.elementType.isRequired,
};
