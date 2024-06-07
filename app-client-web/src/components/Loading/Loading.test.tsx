import { render } from '@testing-library/react';
import { vi } from 'vitest';

import Loading from '@/components/Loading';

vi.mock('react-i18next', () => ({
    useTranslation: () => ({
        t: (str: string) => str,
        i18n: {
            changeLanguage: () => new Promise(() => { /* eslint-disable-line @typescript-eslint/no-empty-function */ }),
        },
    }),
}));

describe('Loading component', () => {
    test('should render without crashing', () => {
        render(<Loading />);
    });
});
