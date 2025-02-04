import { Suspense } from 'react';
import { Outlet } from 'react-router';

import Container from '@mui/material/Container';

import Header from '@/components/Footer';
import Footer from '@/components/Header';
import Loader from '@/components/Loader';

export default function Layout() {
    return (
        <>
            <Header />
            <Container component="main" sx={{ p: 5 }}>
                <Suspense fallback={<Loader />}>
                    <Outlet />
                </Suspense>
            </Container>
            <Footer />
        </>
    );
}
