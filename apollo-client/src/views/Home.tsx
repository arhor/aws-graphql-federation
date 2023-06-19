import { useTranslation } from 'react-i18next';
import { useNavigate } from 'react-router-dom';

import Button from '@mui/material/Button';
import Paper from '@mui/material/Paper';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';

import Loading from '@/components/Loading/Loading';
import useCurrentUser from '@/hooks/useCurrentUser';
import useTopics from '@/hooks/useTopics';

const Home = () => {
    const navigate = useNavigate();
    const { t } = useTranslation();
    const { loading, data, previousData } = useTopics();
    const { data: currentUserData } = useCurrentUser();

    if (loading) {
        return (
            <Loading />
        );
    }

    return (
        <TableContainer component={Paper}>
            {currentUserData?.currentUser && (
                <Button color="inherit" onClick={() => navigate('/create-topic')}>
                    {t('Create New Topic')}
                </Button>
            )}
            <Table>
                <TableHead>
                    <TableRow>
                        <TableCell>{t('Id')}</TableCell>
                        <TableCell>{t('Name')}</TableCell>
                        <TableCell>{t('Content')}</TableCell>
                    </TableRow>
                </TableHead>
                <TableBody>
                    {(data ?? previousData)?.topics.map(({ id, name, posts }) => (
                        <TableRow key={id}>
                            <TableCell>{id}</TableCell>
                            <TableCell>{name}</TableCell>
                            <TableCell>{posts?.[0]?.content.substring(0, 20)}</TableCell>
                        </TableRow>
                    ))}
                </TableBody>
            </Table>
        </TableContainer>
    );
};

export default Home;
