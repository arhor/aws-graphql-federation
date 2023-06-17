package com.github.arhor.dgs.users.data.entity.converter

import com.github.arhor.dgs.users.data.entity.Setting
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import java.util.EnumSet

@ReadingConverter
object SettingsReadingConverter : Converter<Long, EnumSet<Setting>> {

    override fun convert(source: Long): EnumSet<Setting> =
        Setting.emptySet().apply {
            for (setting in Setting.values()) {
                if ((source and (1L shl setting.index)) != 0L) {
                    add(setting)
                }
            }
        }
}

