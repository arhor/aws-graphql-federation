import { useTranslation } from 'react-i18next';
import { Link as RouterLink, useNavigate, useSearchParams } from 'react-router';

import LockOutlinedIcon from '@mui/icons-material/LockOutlined';
import Avatar from '@mui/material/Avatar';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Grid from '@mui/material/Grid';
import Link from '@mui/material/Link';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';

import useSignInMutation from '@/hooks/useSignInMutation';

export default function SignInForm() {
    const navigate = useNavigate();
    const { t } = useTranslation();
    const { signIn } = useSignInMutation();
    const [ searchParams ] = useSearchParams();
    const hasError = searchParams.has('auth') && searchParams.get('auth') === 'failure';

    const handleSubmit = async (e) => {
        e.preventDefault();

        const formData = new FormData(e.currentTarget);

        const username = formData.get('username');
        const password = formData.get('password');

        if (username && password) {
            await signIn({
                variables: {
                    input: {
                        username,
                        password,
                    }
                }
            });
            navigate('/');
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
                {t('forms:sign-in:title')}
            </Typography>
            <Box component="form" onSubmit={handleSubmit} noValidate sx={{ mt: 1 }}>
                <Grid container justifyContent="center">
                    <Grid item xs={10}>
                        <TextField
                            id="username"
                            name="username"
                            label={t('forms:common:fields:username')}
                            margin="normal"
                            required
                            fullWidth
                            autoComplete="username"
                            sx={{ mb: 5 }}
                        />
                    </Grid>
                    <Grid item xs={10}>
                        <TextField
                            id="password"
                            name="password"
                            type="password"
                            label={t('forms:common:fields:password')}
                            margin="normal"
                            required
                            fullWidth
                            error={hasError}
                            helperText={hasError ? 'Incorrect Username or Password' : undefined}
                            autoComplete="current-password"
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
                            {t('forms:sign-in:submit')}
                        </Button>
                    </Grid>
                    <Grid item>
                        <Link to="/sign-up" state={{ doNotCallAuth: true }} component={RouterLink} variant="body2">
                            {t('forms:sign-in:sign-up-link')}
                        </Link>
                    </Grid>
                </Grid>
            </Box>
        </Box>
    );
}
