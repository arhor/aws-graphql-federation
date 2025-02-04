import { createContext, useContext } from 'react';

const AppThemeControlContext = createContext({ 
    switchColorMode: () => {} 
});

export default AppThemeControlContext;

export function useAppThemeControl() {
    return useContext(AppThemeControlContext);
}
