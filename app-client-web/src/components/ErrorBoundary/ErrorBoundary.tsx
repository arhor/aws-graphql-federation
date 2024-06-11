import { Component, ErrorInfo, ReactNode } from 'react';

import { Translation } from 'react-i18next';

import StatelessWidget from '@/components/StatelessWidget';

const DEFAULT_TITLE = 'Ups, something went wrong...';
const DEFAULT_DESCRIPTION = 'Please, contact system administrator if you have nothing else to do';

type Props = {
    t: (text: string) => string;
    children: ReactNode;
}

type State = {
    error: Error | null;
    errorInfo: ErrorInfo | null;
};

class ErrorBoundaryWithTranslation extends Component<Props, State> {
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
        const { t } = this.props;

        if (errorInfo) {
            const [title, description] = process.env.NODE_ENV === 'development'
                ? [error?.toString() ?? DEFAULT_TITLE, errorInfo.componentStack]
                : [t(DEFAULT_TITLE), t(DEFAULT_DESCRIPTION)];

            return <StatelessWidget title={title} description={description} />;
        }
        return this.props.children;
    }
}

export default function ErrorBoundary(props: { children: ReactNode }) {
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
