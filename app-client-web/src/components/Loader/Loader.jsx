import { useTranslation } from 'react-i18next';

import CircularProgress from '@mui/material/CircularProgress';

import { StatelessWidget } from '@/components';

export default function Loading() {
    const { t } = useTranslation();
    return (
        <StatelessWidget
            image={<CircularProgress />}
            title={`${t('Loading')}...`}
        />
    );
}
