import { useTranslation } from 'react-i18next';

import Paper from '@mui/material/Paper';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';

import usePostsPage from '@/hooks/usePostsPage.ts';

export default function Feed() {
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
                    {data?.posts?.data?.map((it: any) => (
                        <TableRow key={it.id}>
                            <TableCell>{it.id}</TableCell>
                            <TableCell>{it.title}</TableCell>
                        </TableRow>
                    ))}
                </TableBody>
            </Table>
        </TableContainer>
    );
}
