import { render } from '@testing-library/react';
import { vi } from 'vitest';

import { Loader } from '@/components';

vi.mock('react-i18next', () => ({
    useTranslation: () => ({
        t: (str) => str,
        i18n: {
            changeLanguage: () => new Promise(() => { }),
        },
    }),
}));

describe('Loader component', () => {
    test('should render without crashing', () => {
        render(<Loader />);
    });
});
