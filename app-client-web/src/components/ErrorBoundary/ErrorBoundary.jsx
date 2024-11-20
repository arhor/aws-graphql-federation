import { Component } from 'react';
import { Translation } from 'react-i18next';

import PropTypes from 'prop-types';

import StatelessWidget from '@/components/StatelessWidget';

const DEFAULT_TITLE = 'Ups, something went wrong...';
const DEFAULT_DESCRIPTION = 'Please, contact system administrator if you have nothing else to do';

ErrorBoundaryWithTranslation.propTypes = {
    t: PropTypes.func,
    children: PropTypes.element.isRequired,
};
class ErrorBoundaryWithTranslation extends Component {
    state = {
        error: null,
        errorInfo: null,
    };

    static getDerivedStateFromError(error) {
        return { error };
    }

    componentDidCatch(error, errorInfo) {
        this.setState({ error, errorInfo });
    }

    render() {
        const { error, errorInfo } = this.state;
        const { t } = this.props;

        if (errorInfo) {
            const [title, description] = process.env.NODE_ENV === 'development'
                ? [error?.toString() ?? DEFAULT_TITLE, errorInfo.componentStack ?? DEFAULT_DESCRIPTION]
                : [t(DEFAULT_TITLE), t(DEFAULT_DESCRIPTION)];

            return <StatelessWidget title={title} description={description} />;
        }
        return this.props.children;
    }
}

ErrorBoundary.propTypes = {
    children: PropTypes.element.isRequired,
};

export default function ErrorBoundary(props) {
    return (
        <Translation>
            {
                (t) => (
                    <ErrorBoundaryWithTranslation t={t}>
                        {props.children}
                    </ErrorBoundaryWithTranslation>
                )
            }
        </Translation>
    );
}
