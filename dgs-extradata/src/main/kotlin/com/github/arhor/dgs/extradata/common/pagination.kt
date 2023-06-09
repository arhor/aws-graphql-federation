package com.github.arhor.dgs.extradata.common

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

class OffsetBasedPageRequest(offset: Offset, limit: Limit, sort: Sort = Sort.unsorted()) : PageRequest(
    /* page = */ offset.value / limit.value,
    /* size = */ limit.value,
    /* sort = */ sort
)

@JvmInline
value class Offset(val value: Int) {
    init {
        require(value >= 0) { "Offset value cannot be less than zero" }
    }

    companion object {
        fun of(value: Int) = Offset(value)
    }
}

@JvmInline
value class Limit(val value: Int) {
    init {
        require(value >= 1) { "Limit value cannot be less than one" }
    }

    companion object {
        fun of(value: Int) = Limit(value)
    }
}
