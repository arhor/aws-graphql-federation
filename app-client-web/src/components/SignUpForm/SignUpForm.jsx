import { useTranslation } from 'react-i18next';
import { Link as RouterLink, useNavigate } from 'react-router-dom';

import LockOutlinedIcon from '@mui/icons-material/LockOutlined';
import Avatar from '@mui/material/Avatar';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Grid from '@mui/material/Grid';
import Link from '@mui/material/Link';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';

import useCreateUserMutation from '@/hooks/useCreateUserMutation';

export default function SignUpForm() {
    const navigate = useNavigate();
    const { t } = useTranslation();
    const { createUser } = useCreateUserMutation();

    /**
     * @param {import('react').FormEvent<HTMLFormElement>} e 
     */
    const handleSubmit = async (e) => {
        e.preventDefault();

        const formData = new FormData(e.currentTarget);

        /** @type {string | null | undefined} */
        const username = formData.get('username');
        /** @type {string | null | undefined} */
        const password = formData.get('password');

        if (username && password) {
            await createUser({
                variables: {
                    input: {
                        username,
                        password,
                    }
                }
            });
            navigate('/sign-in');
        }
    };

    return (
        <Box
            sx={{
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center',
                justifyContent: 'center',
                height: '100vh',
            }}
        >
            <Avatar sx={{ m: 1, bgcolor: 'secondary.main' }}>
                <LockOutlinedIcon />
            </Avatar>
            <Typography component="h1" variant="h5">
                {t('Sign up')}
            </Typography>
            <Box component="form" onSubmit={handleSubmit} noValidate sx={{ mt: 1 }}>
                <Grid container justifyContent="center">
                    <Grid item xs={10}>
                        <TextField
                            id="username"
                            name="username"
                            label={t('Username')}
                            margin="normal"
                            required
                            fullWidth
                            sx={{ mb: 5 }}
                        />
                    </Grid>
                    <Grid item xs={10}>
                        <TextField
                            id="password"
                            name="password"
                            type="password"
                            label={t('Password')}
                            margin="normal"
                            required
                            fullWidth
                            sx={{ mb: 5 }}
                        />
                    </Grid>
                    <Grid item xs={10}>
                        <Button
                            type="submit"
                            fullWidth
                            variant="contained"
                            sx={{ mt: 3, mb: 2 }}
                        >
                            {t('Sign Up')}
                        </Button>
                    </Grid>
                    <Grid item>
                        <Link to="/sign-in" component={RouterLink} variant="body2">
                            {t('Already have an account? Sign in')}
                        </Link>
                    </Grid>
                </Grid>
            </Box>
        </Box>
    );
}
