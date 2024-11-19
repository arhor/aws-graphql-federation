import { SnackbarProvider } from 'notistack';
import PropTypes from 'prop-types';

AppSnackbarProvider.propTypes = {
    children: PropTypes.element.isRequired,
};

export default function AppSnackbarProvider(props) {
    return (
        <SnackbarProvider preventDuplicate>
            {props.children}
        </SnackbarProvider>
    );
}
