package com.github.arhor.dgs.users.data.entity.converter

import com.github.arhor.dgs.users.generated.graphql.types.Setting
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import java.util.EnumSet

@ReadingConverter
object SettingsReadingConverter : Converter<Long, EnumSet<Setting>> {

    override fun convert(source: Long): EnumSet<Setting> =
        EnumSet.noneOf(Setting::class.java).apply {
            for (setting in Setting.values()) {
                if ((source and (1L shl setting.ordinal)) != 0L) {
                    add(setting)
                }
            }
        }
}

