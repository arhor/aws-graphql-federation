import { useTranslation } from 'react-i18next';

import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import Chip from '@mui/material/Chip';
import Paper from '@mui/material/Paper';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Typography from '@mui/material/Typography';

import { usePostsPage } from '@/hooks';

export default function Home() {
    const { t } = useTranslation();
    const { data } = usePostsPage();

    const posts = [
        {
            id: '1',
            title: 'First Post',
            content: 'This is the content of the first post...',
            tags: ['announcement', 'news']
        },
        {
            id: '2',
            title: 'Second Post',
            content: 'Here we discuss the project updates...',
            tags: ['update', 'discussion']
        }
    ];

    const View = () => (
        <Box mt={2}>
            <Button variant="contained" color="primary" sx={{ mb: 2 }}>
                Create New Post
            </Button>
            {posts.map((post) => (
                <Card key={post.id} sx={{ mb: 2 }}>
                    <CardContent>
                        <Typography variant="h6">{post.title}</Typography>
                        <Typography variant="body2" color="textSecondary" sx={{ mb: 1 }}>
                            {post.content}
                        </Typography>
                        <Box>
                            {post.tags.map((tag, index) => (
                                <Chip key={index} label={tag} size="small" sx={{ mr: 1 }} />
                            ))}
                        </Box>
                    </CardContent>
                </Card>
            ))}
        </Box>
    );

    return (
        <>
            <View />
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
            </TableContainer></>
    );
}
