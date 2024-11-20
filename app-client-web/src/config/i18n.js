
import { initReactI18next } from 'react-i18next';

import i18n from 'i18next';
import httpBackend from 'i18next-http-backend';

i18n.use(httpBackend)
    .use(initReactI18next)
    .init({
        lng: 'en',
        fallbackLng: 'en',
        load: 'languageOnly',
        preload: ['en'],
        interpolation: {
            escapeValue: false,
        },
        react: {
            useSuspense: true,
        },
    });

export default i18n;
