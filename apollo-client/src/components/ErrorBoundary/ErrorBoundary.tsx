import { Component, ErrorInfo, ReactNode } from 'react';

import StatelessWidget from '@/components/StatelessWidget';

const DEFAULT_TITLE = 'Ups, something went wrong...';
const DEFAULT_DESCRIPTION = 'Please, contact system administrator if you have nothing else to do';

export type Props = {
    children: ReactNode;
}

export type State = {
    error: Error | null;
    errorInfo: ErrorInfo | null;
};

class ErrorBoundary extends Component<Props, State> {
    state: State = {
        error: null,
        errorInfo: null,
    };

    static getDerivedStateFromError(error: Error) {
        return { error };
    }

    componentDidCatch(error: Error, errorInfo: ErrorInfo) {
        this.setState({ error, errorInfo });
    }

    render() {
        const { error, errorInfo } = this.state;

        if (errorInfo) {
            const [ title, description ] = process.env.NODE_ENV === 'development'
                ? [ error?.toString() ?? DEFAULT_TITLE, errorInfo.componentStack ]
                : [ DEFAULT_TITLE, DEFAULT_DESCRIPTION ];

            return <StatelessWidget title={title} description={description} />;
        }
        return this.props.children;
    }
}

export default ErrorBoundary;
