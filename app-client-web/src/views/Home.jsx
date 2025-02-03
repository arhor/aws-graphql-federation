import { useTranslation } from 'react-i18next';

import Paper from '@mui/material/Paper';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';

import { usePostsPage } from '@/hooks';

export default function Home() {
    const { t } = useTranslation();
    const { data } = usePostsPage();

    return (
        <TableContainer component={Paper}>
            <Table>
                <TableHead>
                    <TableRow>
                        <TableCell>{t('Id')}</TableCell>
                        <TableCell>{t('Header')}</TableCell>
                    </TableRow>
                </TableHead>
                <TableBody>
                    {data?.posts?.data?.map(({ id, title }) => (
                        <TableRow key={id}>
                            <TableCell>{id}</TableCell>
                            <TableCell>{title}</TableCell>
                        </TableRow>
                    ))}
                </TableBody>
            </Table>
        </TableContainer>
    );
}
