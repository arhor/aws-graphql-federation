package com.github.arhor.dgs.users.graphql.fetcher

import com.github.arhor.dgs.users.data.entity.Setting
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import java.util.EnumSet

@DgsComponent
class SettingsFetcher {

    @DgsQuery
    fun availableUserSettings(): EnumSet<Setting> {
        return EnumSet.allOf(Setting::class.java)
    }
}
