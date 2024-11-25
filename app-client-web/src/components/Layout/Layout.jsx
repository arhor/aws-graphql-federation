import { Suspense } from 'react';
import { Outlet } from 'react-router-dom';

import Container from '@mui/material/Container';

import { Footer, Header, Loader } from '@/components';

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
