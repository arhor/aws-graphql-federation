import React from 'react';

import { Translation } from 'react-i18next';

import StatelessWidget from '@/components/StatelessWidget';

const DEFAULT_TITLE = 'Ups, something went wrong...';
const DEFAULT_DESCRIPTION = 'Please, contact system administrator if you have nothing else to do';

/**
 * @typedef {Object} Props
 * @property {(text: string) => string} t
 * @property {React.ReactNode} children - Children nodes to render.
 */

/**
 * @typedef {Object} State
 * @property {Error | null} error
 * @property {React.ErrorInfo | null} errorInfo
 */

class ErrorBoundaryWithTranslation extends React.Component {
    /**
     * @param {Props} props
     */
    constructor(props) {
        super(props);

        /** @type {State} */
        this.state = {
            error: null,
            errorInfo: null,
        };
    }
    
    /**
     * @param {Error} error
     * @returns {Partial<State>}
     */
    static getDerivedStateFromError(error) {
        return { error };
    }

    /**
     * @param {Error} error
     * @param {React.ErrorInfo} errorInfo
     */
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

/**
 * @param {{ children: React.ReactNode }} props
 * @returns {JSX.Element}
 */
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
