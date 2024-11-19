import React from 'react';

import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';

/**
 * @typedef {'page' | 'card'} Type
 * @typedef {'small' | 'medium' | 'large'} Size
 * @typedef {'h4' | 'h5' | 'h6'} Variant
 */

/**
 * @typedef {Object} WidgetParams
 * @property {number} imageWidth
 * @property {number} imageHeight
 * @property {Variant} variant
 */

/**
 * Props for StatelessWidget.
 * @typedef {Object} Props
 * @property {Type} [type='page'] - Type of widget, either 'page' or 'card'.
 * @property {Size} [size='large'] - Size of widget, either 'small', 'medium', or 'large'.
 * @property {number} [padding=2] - Padding around the widget.
 * @property {React.ReactElement} [image] - Image to display in the widget.
 * @property {string} [title] - Title of the widget.
 * @property {string} [description] - Description text for the widget.
 * @property {React.ReactElement} [button] - Button to include in the widget.
 */

/**
 * Stateless widget component.
 * @param {Props} props - The props for the widget.
 * @returns {React.ReactElement} The rendered stateless widget.
 */
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

/**
 * @param {Size} size
 * @returns {WidgetParams}
 */
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

/**
 * Determines the box style based on type and padding.
 * @param {Type} type - The type of the widget ('page' or 'card').
 * @param {number} padding - The padding for the box.
 * @returns {import('@mui/system').SxProps} The style object for the box.
 */
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
