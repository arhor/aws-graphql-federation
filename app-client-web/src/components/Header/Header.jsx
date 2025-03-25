import { useTranslation } from 'react-i18next';
import { Link as RouterLink } from 'react-router';

import AccountCircleIcon from '@mui/icons-material/AccountCircle';
import ForumIcon from '@mui/icons-material/Forum';
import MenuIcon from '@mui/icons-material/Menu';
import NotificationsIcon from '@mui/icons-material/Notifications';
import SearchIcon from '@mui/icons-material/Search';
import AppBar from '@mui/material/AppBar';
import Box from '@mui/material/Box';
import FormControlLabel from '@mui/material/FormControlLabel';
import IconButton from '@mui/material/IconButton';
import InputBase from '@mui/material/InputBase';
import { alpha, styled, useTheme } from '@mui/material/styles';
import Switch from '@mui/material/Switch';
import Toolbar from '@mui/material/Toolbar';
import Typography from '@mui/material/Typography';

import { useAppThemeControl } from '@/theme';

const Offset = styled('div')(({ theme }) => theme.mixins.toolbar);

const Search = styled('div')(({ theme }) => ({
  position: 'relative',
  borderRadius: theme.shape.borderRadius,
  backgroundColor: alpha(theme.palette.common.white, 0.15),
  '&:hover': {
    backgroundColor: alpha(theme.palette.common.white, 0.25),
  },
  marginRight: theme.spacing(2),
  marginLeft: 0,
  width: '100%',
  [theme.breakpoints.up('sm')]: {
    marginLeft: theme.spacing(3),
    width: 'auto',
  },
}));

const SearchIconWrapper = styled('div')(({ theme }) => ({
  padding: theme.spacing(0, 2),
  height: '100%',
  position: 'absolute',
  pointerEvents: 'none',
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
}));

const StyledInputBase = styled(InputBase)(({ theme }) => ({
  color: 'inherit',
  '& .MuiInputBase-input': {
    padding: theme.spacing(1, 1, 1, 0),
    paddingLeft: `calc(1em + ${theme.spacing(4)})`,
    transition: theme.transitions.create('width'),
    width: '100%',
    [theme.breakpoints.up('md')]: {
      width: '20ch',
    },
  },
}));

export default function Header() {
    const theme = useTheme();
    const { t } = useTranslation();
    const { switchColorMode } = useAppThemeControl();

    return (
        <>
            <AppBar position="fixed">
                <Toolbar>
                    <IconButton
                        edge="start"
                        color="inherit"
                        aria-label="menu"
                        sx={{ mr: 1 }}
                    >
                        <MenuIcon />
                    </IconButton>
                    <IconButton 
                        color="inherit" 
                        component={RouterLink} 
                        to="/"
                        sx={{ display: 'flex', alignItems: 'center' }}
                    >
                        <ForumIcon sx={{ mr: 1 }} />
                        <Typography variant="h6" noWrap component="div" sx={{ display: { xs: 'none', sm: 'block' } }}>
                            PostIt
                        </Typography>
                    </IconButton>
                    
                    <Search>
                        <SearchIconWrapper>
                            <SearchIcon />
                        </SearchIconWrapper>
                        <StyledInputBase
                            placeholder={t('Search…')}
                            inputProps={{ 'aria-label': 'search' }}
                        />
                    </Search>
                    
                    <Box sx={{ flexGrow: 1 }} />
                    
                    <Box sx={{ display: 'flex', alignItems: 'center' }}>
                        <FormControlLabel
                            label={theme.palette.mode === 'dark' ? t('Dark') : t('Light')}
                            sx={{ mr: 1 }}
                            control={
                                <Switch
                                    checked={theme.palette.mode === 'dark'}
                                    onChange={switchColorMode}
                                    size="small"
                                />
                            }
                        />
                        
                        <IconButton color="inherit">
                            <NotificationsIcon />
                        </IconButton>
                        
                        <IconButton 
                            color="inherit"
                            component={RouterLink}
                            to="/settings"
                            sx={{ ml: 1 }}
                        >
                            <AccountCircleIcon />
                        </IconButton>
                    </Box>
                </Toolbar>
            </AppBar>
            <Offset />
        </>
    );
}
