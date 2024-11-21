import { createContext, useContext } from 'react';

export const AppThemeControlContext = createContext({ 
    switchColorMode: () => {} 
});

export function useAppThemeControl() {
    return useContext(AppThemeControlContext);
}
