package com.github.arhor.dgs.users.data.entity

import java.util.EnumSet

const val MIN_SETTING_INDEX = 0
const val MAX_SETTING_INDEX = Long.SIZE_BITS - 1

private val INDEX_RANGE = MIN_SETTING_INDEX..MAX_SETTING_INDEX

enum class Setting(val index: Int) {
    // @formatter:off
    AGE_OVER_18        (index = 0),
    GORGEOUS_CAT_OWNER (index = 1),
    STAR_WARS_LOVER    (index = 2),
    // @formatter:on
    ;

    init {
        require(index in INDEX_RANGE) {
            "'index' value must be in range $INDEX_RANGE, but $index is used for $name"
        }
    }

    companion object {
        fun emptySet(): EnumSet<Setting> = EnumSet.noneOf(Setting::class.java)
    }
}
