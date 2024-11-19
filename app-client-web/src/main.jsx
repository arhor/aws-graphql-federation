import { StrictMode } from 'react';

import { createRoot } from 'react-dom/client';

import App from '@/App';
import '@/config/i18n';

createRoot(document.getElementById('root')).render(
    <StrictMode>
        <App />
    </StrictMode>
);
