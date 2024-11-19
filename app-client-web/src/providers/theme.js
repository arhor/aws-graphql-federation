import { createContext, useContext } from 'react';

/**
 * @typedef {'light' | 'dark'} AppThemeMode
 */

/**
 * @typedef {Object} AppThemeControl
 * @property {() => void} switchColorMode
 */

/**
 * A React context for AppThemeControl.
 * @type {React.Context<AppThemeControl>}
 */
export const AppThemeControlContext = createContext({ 
    switchColorMode: () => {} 
});

/**
 * A hook to use the AppThemeControl context.
 * @returns {AppThemeControl} The current theme control object.
 */
export function useAppThemeControl() {
    return useContext(AppThemeControlContext);
}
