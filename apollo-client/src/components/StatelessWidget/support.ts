import { SxProps } from '@mui/system';

export type Type = 'page' | 'card';
export type Size = 'small' | 'medium' | 'large';
export type Variant = 'h4' | 'h5' | 'h6';

export function determineWidgetParams(size: Size): {
    imageWidth: number,
    imageHeight: number,
    variant: Variant,
} {
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

export function determineBoxStyle(type: Type, padding: number): SxProps {
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
