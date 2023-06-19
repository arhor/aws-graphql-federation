import { useTranslation } from 'react-i18next';
import { useNavigate } from 'react-router';

import Button from '@mui/material/Button';

import StatelessWidget from '@/components/StatelessWidget';

const NotFound = () => {
    const navigate = useNavigate();
    const { t } = useTranslation();

    const handleButtonClick = () => {
        navigate('/');
    };

    return (
        <StatelessWidget
            type="page"
            size="large"
            title={`${t('Ups, page not found')}...`}
            description={`${t('Please, try to find somewhere else')} :)`}
            button={
                <Button variant="outlined" onClick={handleButtonClick}>
                    {t('bring me home')}
                </Button>
            }
        />
    );
};

export default NotFound;
