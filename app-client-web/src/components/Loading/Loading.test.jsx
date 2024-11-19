import { render } from '@testing-library/react';
import { vi } from 'vitest';

import Loading from '@/components/Loading';

vi.mock('react-i18next', () => ({
    useTranslation: () => ({
        /**
         * @param {string} str 
         * @returns {string}
         */
        t: (str) => str,
        i18n: {
            changeLanguage: () => new Promise(() => {}),
        },
    }),
}));

describe('Loading component', () => {
    test('should render without crashing', () => {
        render(<Loading />);
    });
});
