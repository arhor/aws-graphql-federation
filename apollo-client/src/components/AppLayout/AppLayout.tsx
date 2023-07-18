import { Outlet } from 'react-router-dom';

import Container from '@mui/material/Container';

import Footer from '@/components/Footer';
import Header from '@/components/Header';

function AppLayout() {
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

export default AppLayout;
