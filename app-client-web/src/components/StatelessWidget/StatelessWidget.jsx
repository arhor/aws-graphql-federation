import PropTypes from 'prop-types'

import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';

export default function StatelessWidget({
    type = 'page',
    size = 'large',
    padding = 2,
    image,
    title,
    description,
    button,
}) {
    const boxStyle = determineBoxStyle(type, padding);
    const { imageWidth, imageHeight, variant } = determineWidgetParams(size);

    return (
        <Box sx={boxStyle}>
            {image && (
                <Box sx={{ mb: title || description ? 2 : 0, width: `${imageWidth}%`, height: `${imageHeight}%` }}>
                    {image}
                </Box>
            )}
            {title && (
                <Box mb={!description && button ? 2 : 0}>
                    <Typography variant={variant}>{title}</Typography>
                </Box>
            )}
            {description && (
                <Box mb={button && 2}>
                    <Typography variant="body1">{description}</Typography>
                </Box>
            )}
            {button && button}
        </Box>
    );
}

StatelessWidget.propTypes = {
    type: PropTypes.oneOf('page', 'card'),
    size: PropTypes.oneOf('small', 'medium', 'large'),
    padding: PropTypes.number,
    image: PropTypes.element,
    title: PropTypes.string,
    description: PropTypes.string,
    button: PropTypes.element,
};
StatelessWidget.defaultProps = {
    type: 'page',
    size: 'large',
    padding: 2,
};

function determineWidgetParams(size) {
    switch (size) {
        case 'small':
            return {
                imageWidth: 40,
                imageHeight: 40,
                variant: 'h6',
            };
        case 'large':
            return {
                imageWidth: 100,
                imageHeight: 100,
                variant: 'h4',
            };
        case 'medium':
        default:
            return {
                imageWidth: 60,
                imageHeight: 60,
                variant: 'h5',
            };
    }
}

function determineBoxStyle(type, padding) {
    switch (type) {
        case 'card':
            return {
                padding,
                textAlign: 'center'
            };
        case 'page':
            return {
                transform: 'translate(-50%, -50%)',
                position: 'absolute',
                top: '50%',
                left: '50%',
                textAlign: 'center',
            };
        default:
            throw new Error(`Unsupported type: ${type}`);
    }
}
