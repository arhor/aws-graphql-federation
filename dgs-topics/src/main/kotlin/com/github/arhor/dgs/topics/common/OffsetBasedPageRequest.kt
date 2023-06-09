package com.github.arhor.dgs.topics.common

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

data class OffsetBasedPageRequest(
    val limit: Int,
    val offset: Int,
    val sort: Sort = Sort.unsorted(),
) : PageRequest(
    /* page = */ offset / limit,
    /* size = */ limit,
    /* sort = */ sort
)
