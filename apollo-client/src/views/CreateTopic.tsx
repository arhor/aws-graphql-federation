import { FormEvent } from 'react';

import { useNavigate } from 'react-router-dom';

import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Grid from '@mui/material/Grid';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';

import useCreateTopicMutation from '@/hooks/useCreateTopicMutation';
import useCurrentUser from '@/hooks/useCurrentUser';
import { Optional } from '@/utils/core-utils';

const CreateTopic = () => {
    const navigate = useNavigate();
    const { data } = useCurrentUser();
    const { createTopic } = useCreateTopicMutation();

    const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
        e.preventDefault();

        const formData = new FormData(e.currentTarget);

        const name = formData.get('name') as Optional<string>;

        if (name) {
            await createTopic({
                variables: {
                    name: name,
                    userId: data!.currentUser!.id,
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
            <Typography component="h1" variant="h5">
                Create Topic
            </Typography>
            <Box component="form" onSubmit={handleSubmit} noValidate sx={{ mt: 1 }}>
                <Grid container justifyContent="center">
                    <Grid item xs={10}>
                        <TextField
                            id="name"
                            name="name"
                            label="Name"
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
                            {'Create Topic'}
                        </Button>
                    </Grid>
                </Grid>
            </Box>
        </Box>
    );
};

export default CreateTopic;
