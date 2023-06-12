package com.github.arhor.dgs.lib

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

class OffsetBasedPageRequest(limit: Int, offset: Int, sort: Sort = Sort.unsorted()) : PageRequest(
    /* page = */ offset / limit,
    /* size = */ limit,
    /* sort = */ sort
) {
    init {
        require(limit >= 1) { "Limit value cannot be less than one" }
        require(offset >= 0) { "Offset value cannot be less than zero" }
    }
}
