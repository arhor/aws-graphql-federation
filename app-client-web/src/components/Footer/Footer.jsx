import { useEffect, useState } from 'react';
import { Link as RouterLink, useLocation } from 'react-router';

import ExploreIcon from '@mui/icons-material/Explore';
import HomeIcon from '@mui/icons-material/Home';
import NotificationsIcon from '@mui/icons-material/Notifications';
import PostAddIcon from '@mui/icons-material/PostAdd';
import WhatshotIcon from '@mui/icons-material/Whatshot';
import BottomNavigation from '@mui/material/BottomNavigation';
import BottomNavigationAction from '@mui/material/BottomNavigationAction';
import Paper from '@mui/material/Paper';

const buttons = [
    {
        icon: HomeIcon,
        label: 'Home',
        path: '/',
    },
    {
        icon: WhatshotIcon,
        label: 'Popular',
        path: '/popular',
    },
    {
        icon: PostAddIcon,
        label: 'Create',
        path: '/create-post',
    },
    {
        icon: NotificationsIcon,
        label: 'Notifications',
        path: '/notifications',
    },
    {
        icon: ExploreIcon,
        label: 'Explore',
        path: '/explore',
    },
];

export default function Footer() {
    const { pathname } = useLocation();
    const [value, setValue] = useState();

    useEffect(() => {
        setValue(buttons.findIndex(it => it.path === pathname));
    }, [pathname]);

    return (
        <Paper sx={{ position: 'fixed', bottom: 0, left: 0, right: 0, zIndex: 100 }} elevation={3}>
            <BottomNavigation value={value} onChange={(_, newValue) => setValue(newValue)}>
                {buttons.map(it => (
                    <BottomNavigationAction
                        to={it.path}
                        key={it.path}
                        label={it.label}
                        icon={<it.icon />}
                        component={RouterLink}
                    />
                ))}
            </BottomNavigation>
        </Paper>
    );
}
