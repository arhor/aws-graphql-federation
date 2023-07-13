import { BrowserRouter, Route, Routes } from 'react-router-dom';

import Container from '@mui/material/Container';

import Footer from '@/components/Footer';
import Header from '@/components/Header';
import Home from '@/views/Home';
import NotFound from '@/views/NotFound';
import SignIn from '@/views/SignIn';
import SignUp from '@/views/SignUp';

const AppLayout = () => {
    return (
        <BrowserRouter>
            <Header />
            <Container component="main" sx={{ p: 5 }}>
                <Routes>
                    <Route index           element={<Home />} />
                    <Route path="/sign-in" element={<SignIn />} />
                    <Route path="/sign-up" element={<SignUp />} />
                    <Route path="*"        element={<NotFound />} />
                </Routes>
            </Container>
            <Footer />
        </BrowserRouter>
    );
};

export default AppLayout;
