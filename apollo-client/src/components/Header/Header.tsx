import { useTranslation } from 'react-i18next';
import { useNavigate } from 'react-router-dom';


import AppBar from '@mui/material/AppBar';
import Button from '@mui/material/Button';
import FormControlLabel from '@mui/material/FormControlLabel';
import { styled, useTheme } from '@mui/material/styles';
import Switch from '@mui/material/Switch';
import Toolbar from '@mui/material/Toolbar';
import Typography from '@mui/material/Typography'

import { useAppThemeControl } from '@/AppThemeProvider';
import useCurrentUser from '@/hooks/useCurrentUser';

const Offset = styled('div')(({ theme }) => theme.mixins.toolbar);

const Header = () => {
    const theme = useTheme();
    const navigate = useNavigate();
    const { t } = useTranslation();
    const { loading, data } = useCurrentUser();
    const { switchColorMode } = useAppThemeControl();

    const displaySignButton = () => {
        if (loading) {
            return null;
        }
        return data?.currentUser ? (
            <Button color="inherit" href="/api/sign-out">
                {t('Sign-Out')}
            </Button>
        ) : (
            <Button color="inherit" onClick={() => navigate('/sign-in')}>
                {t('Sign-In')}
            </Button>
        );
    };

    return (
        <>
            <AppBar position="fixed">
                <Toolbar>
                    <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
                        {t('Netflix DGS + Apollo Client (React)')}
                    </Typography>
                    {displaySignButton()}
                    <FormControlLabel
                        label="Dark mode"
                        control={
                            <Switch
                                checked={theme.palette.mode === 'dark'}
                                onChange={switchColorMode}
                            />
                        }
                    />
                </Toolbar>
            </AppBar>
            <Offset />
        </>
    );
};

export default Header;
