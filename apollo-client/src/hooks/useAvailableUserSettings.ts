import { useEffect, useState } from 'react';

import { useQuery } from '@apollo/client';

import { graphql } from '@/gql';

const GET_AVAILABLE_USER_SETTINGS = graphql(`
    query GetAvailableUserSettings {
        availableUserSettings
    }
`);

const useAvailableUserSettings = () => {
    const { loading, data } = useQuery(GET_AVAILABLE_USER_SETTINGS);
    const [availableUserSettings, setAvailableUserSettings] = useState<{ name: string, checked: boolean }[]>([]);

    useEffect(() => {
        if (data) {
            setAvailableUserSettings(
                data.availableUserSettings.map(setting => {
                    return ({ name: setting, checked: false });
                })
            );
        }
    }, [data]);

    const switchSetting = (name: string) => {
        setAvailableUserSettings(
            availableUserSettings.map(it => {
                return it.name === name
                    ? { name: it.name, checked: !it.checked }
                    : it;
            })
        );
    };

    return { loading, availableUserSettings, switchSetting };
};

export default useAvailableUserSettings;
