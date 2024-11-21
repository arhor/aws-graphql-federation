import { SnackbarProvider } from 'notistack';
import PropTypes from 'prop-types';

export default function AppSnackbarProvider(props) {
    return (
        <SnackbarProvider preventDuplicate>
            {props.children}
        </SnackbarProvider>
    );
}

AppSnackbarProvider.propTypes = {
    children: PropTypes.element.isRequired,
};
