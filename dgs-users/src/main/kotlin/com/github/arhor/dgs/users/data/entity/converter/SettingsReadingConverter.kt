package com.github.arhor.dgs.users.data.entity.converter

import com.github.arhor.dgs.users.data.entity.Setting
import com.github.arhor.dgs.users.data.entity.Settings
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import java.util.EnumSet

@ReadingConverter
object SettingsReadingConverter : Converter<Long, Settings> {

    override fun convert(source: Long): Settings = Settings(
        items = EnumSet.noneOf(Setting::class.java).apply {
            for (setting in Setting.values()) {
                if ((source and (1L shl setting.index)) != 0L) {
                    add(setting)
                }
            }
        }
    )
}

