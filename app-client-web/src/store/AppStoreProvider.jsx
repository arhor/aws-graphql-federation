import { Provider } from 'react-redux';

import PropTypes from 'prop-types';

import store from '@/store';

export default function AppStoreProvider(props) {
    return (
        <Provider store={store}>
            {props.children}
        </Provider>
    );
}

AppStoreProvider.propTypes = {
    children: PropTypes.element.isRequired,
};
