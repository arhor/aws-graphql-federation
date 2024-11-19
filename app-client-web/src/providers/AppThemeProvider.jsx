import { ReactNode, useMemo, useState } from 'react';

import CssBaseline from '@mui/material/CssBaseline';
import { createTheme, ThemeProvider } from '@mui/material/styles';
import useMediaQuery from '@mui/material/useMediaQuery';

import { AppThemeControlContext } from '@/providers/theme';



/**
 * @param {Object} props
 * @param {ReactNode} props.children
 */
export default function AppThemeProvider(props) {
    const [colorMode, setColorMode] = useState();
    const darkThemePreferred = useMediaQuery('(prefers-color-scheme: dark)');

    const theme = useMemo(() => createTheme({
        palette: {
            mode: colorMode ?? determineColorMode(darkThemePreferred),
        },
    }), [colorMode, darkThemePreferred]);

    const switchColorMode = () => {
        setColorMode((prev) => determineColorMode(prev === 'light'));
    };

    return (
        <AppThemeControlContext.Provider value={{ switchColorMode }}>
            <ThemeProvider theme={theme}>
                <CssBaseline />
                {props.children}
            </ThemeProvider>
        </AppThemeControlContext.Provider>
    );
}

/**
 * @param {boolean} shouldUseDarkTheme
 * @returns {import('@/providers/theme').AppThemeMode}
 */
function determineColorMode(shouldUseDarkTheme) {
    return shouldUseDarkTheme ? 'dark' : 'light';
}
