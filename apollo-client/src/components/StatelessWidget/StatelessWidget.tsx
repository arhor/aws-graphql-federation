import { ReactElement } from 'react';

import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';

import { Size, Type, determineBoxStyle, determineWidgetParams } from '@/components/StatelessWidget/support';

export type Props = {
    type?: Type;
    size?: Size;
    padding?: number;
    image?: ReactElement;
    title?: string;
    description?: string;
    button?: ReactElement;
};

export default function StatelessWidget({
    type = 'page',
    size = 'large',
    padding = 2,
    image,
    title,
    description,
    button,
}: Props) {
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
