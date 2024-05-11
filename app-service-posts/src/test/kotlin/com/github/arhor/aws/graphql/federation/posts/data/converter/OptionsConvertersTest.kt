package com.github.arhor.aws.graphql.federation.posts.data.converter

import com.github.arhor.aws.graphql.federation.posts.data.entity.PostEntity
import com.github.arhor.aws.graphql.federation.posts.generated.graphql.types.Option
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.EnumSet
import java.util.stream.Stream

class OptionsConvertersTest {

    @MethodSource
    @ParameterizedTest
    fun `converters should correctly serialize and deserialize input`(
        // Given
        initialData: PostEntity.Options,
    ) {
        // When
        val result = initialData
            .let(OptionsWritingConverter::convert)
            .let(OptionsReadingConverter::convert)

        // Then
        assertThat(result.items)
            .containsExactlyInAnyOrderElementsOf(initialData.items)
    }

    companion object {
        @JvmStatic
        fun `converters should correctly serialize and deserialize input`(): Stream<Arguments> =
            Stream.of(
                arguments(PostEntity.Options()),
                arguments(PostEntity.Options(EnumSet.allOf(Option::class.java))),
            )
    }
}
