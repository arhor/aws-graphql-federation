import { render } from '@testing-library/react';

import { StatelessWidget } from '@/components';

describe('StatelessWidget component', () => {
    test('should render without crashing', () => {
        render(<StatelessWidget />);
    });
});
