package com.github.arhor.aws.graphql.federation.posts.service.mapping.impl

import com.github.arhor.aws.graphql.federation.posts.service.mapping.OptionsMapper
import com.github.arhor.aws.graphql.federation.posts.service.mapping.TagMapper
import io.mockk.mockk

class PostMapperImplTest {

    private val optionsMapper = mockk<OptionsMapper>()
    private val tagMapper = mockk<TagMapper>()

    private val postMapper = PostMapperImpl(
        optionsMapper,
        tagMapper,
    )
}
