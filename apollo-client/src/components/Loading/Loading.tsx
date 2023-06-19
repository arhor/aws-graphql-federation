import { useTranslation } from 'react-i18next';

import CircularProgress from '@mui/material/CircularProgress';

import StatelessWidget from '@/components/StatelessWidget';

const Loading = () => {
    const { t } = useTranslation();

    return (
        <StatelessWidget
            image={<CircularProgress />}
            title={`${t('Loading')}...`}
        />
    );
};

export default Loading;
