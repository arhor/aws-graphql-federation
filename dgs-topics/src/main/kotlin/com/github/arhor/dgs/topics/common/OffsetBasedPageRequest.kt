package com.github.arhor.dgs.topics.common

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

class OffsetBasedPageRequest(
    limit: Int,
    offset: Int,
    sort: Sort = Sort.unsorted(),
) : PageRequest(
    /* page = */ offset / limit,
    /* size = */ limit,
    /* sort = */ sort
)
