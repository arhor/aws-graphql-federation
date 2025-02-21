import { useTranslation } from 'react-i18next';

import {
    Box,
    Button,
    Card,
    CardContent,
    TextField,
    Typography
} from '@mui/material';

export default function UserSettings() {
    const { t } = useTranslation();

    return (
        <Box mt={2}>
            <Card sx={{ maxWidth: 600, margin: 'auto' }}>
                <CardContent>
                    <Typography variant="h5" gutterBottom>
                        {t('User Settings')}
                    </Typography>
                    <TextField fullWidth label="New Password" type="password" margin="normal" />
                    <Button variant="contained" color="primary" sx={{ mt: 2 }}>
                        {t('Save Changes')}
                    </Button>
                </CardContent>
            </Card>
        </Box>
    );
};
