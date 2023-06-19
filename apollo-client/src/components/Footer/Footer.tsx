import { useEffect, useState } from 'react';

import { Link as RouterLink, useLocation } from 'react-router-dom';

import ForumIcon from '@mui/icons-material/Forum';
import UserAddIcon from '@mui/icons-material/PersonAdd';
import PostAddIcon from '@mui/icons-material/PostAdd';
import BottomNavigation from '@mui/material/BottomNavigation';
import BottomNavigationAction from '@mui/material/BottomNavigationAction';
import Paper from '@mui/material/Paper';

const buttons = [
    {
        icon: ForumIcon,
        path: '/',
    },
    {
        icon: UserAddIcon,
        path: '/create-user',
    },
    {
        icon: PostAddIcon,
        path: '/create-post',
    },
];

const Footer = () => {
    const { pathname } = useLocation();
    const [value, setValue] = useState<number>();

    useEffect(() => {
        setValue(buttons.findIndex(it => it.path == pathname));
    }, [pathname]);

    return (
        <Paper sx={{ position: 'fixed', bottom: 0, left: 0, right: 0 }} elevation={3}>
            <BottomNavigation value={value} onChange={(_, newValue) => setValue(newValue)}>
                {buttons.map(it => (
                    <BottomNavigationAction
                        to={it.path}
                        key={it.path}
                        icon={<it.icon />}
                        component={RouterLink}
                    />
                ))}
            </BottomNavigation>
        </Paper>
    );
};

export default Footer;
