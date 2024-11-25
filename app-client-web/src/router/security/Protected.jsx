import PropTypes from 'prop-types';

import { withProtection } from '@/router/security/withProtection';

export default function Protected({ component, authorities, ...rest }) {
    const ProtectedComponent = withProtection(component, authorities);
    return <ProtectedComponent {...rest} />;
}

Protected.propTypes = {
    component: PropTypes.elementType.isRequired,
    authorities: PropTypes.arrayOf(PropTypes.string),
};
