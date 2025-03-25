import { Suspense } from 'react';
import { Outlet } from 'react-router';

import Box from '@mui/material/Box';

import Footer from '@/components/Footer';
import Header from '@/components/Header';
import Loader from '@/components/Loader';

export default function Layout() {
    return (
        <Box sx={{ minHeight: '100vh', paddingBottom: '56px' }}>
            <Header />
            <Box component="main" sx={{ pt: 2, pb: 8 }}>
                <Suspense fallback={<Loader />}>
                    <Outlet />
                </Suspense>
            </Box>
            <Footer />
        </Box>
    );
}
