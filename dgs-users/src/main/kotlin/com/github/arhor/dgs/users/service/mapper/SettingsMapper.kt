package com.github.arhor.dgs.users.service.mapper;

import com.github.arhor.dgs.lib.mapstruct.MapstructCommonConfig
import com.github.arhor.dgs.users.data.entity.Setting
import com.github.arhor.dgs.users.data.entity.Settings
import org.mapstruct.Mapper
import java.util.EnumSet

@Mapper(config = MapstructCommonConfig::class)
abstract class SettingsMapper {

    fun wrap(value: EnumSet<Setting>?): Settings = Settings(value.emptySetIfNull())

    fun unwrap(value: Settings?): EnumSet<Setting> = value?.items.emptySetIfNull()

    private fun EnumSet<Setting>?.emptySetIfNull() = this ?: EnumSet.noneOf(Setting::class.java)
}
