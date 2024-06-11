import { createContext, useContext } from 'react';

export type AppThemeMode = 'light' | 'dark';

export type AppThemeControl = {
    switchColorMode: () => void;
};

export const AppThemeControlContext = createContext({} as AppThemeControl);

export function useAppThemeControl(): AppThemeControl {
    return useContext(AppThemeControlContext);
}
