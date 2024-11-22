import { Outlet } from 'react-router-dom';

import Container from '@mui/material/Container';

import { Footer, Header } from '@/components';

export default function Layout() {
    return (
        <>
            <Header />
            <Container component="main" sx={{ p: 5 }}>
                <Outlet />
            </Container>
            <Footer />
        </>
    );
}
