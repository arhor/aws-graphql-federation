import { useTranslation } from 'react-i18next';

import log from 'loglevel';
import PropTypes from 'prop-types';

import StatelessWidget from '@/components/StatelessWidget';

export default function ErrorWidget({ error }) {
    const { t } = useTranslation();

    log.error(error);
    
    return (
        <StatelessWidget
            title={t('error-widget.title')}
            description={t('error-widget.description')}
        />
    );
}

ErrorWidget.propTypes = {
    error: PropTypes.object,
};
