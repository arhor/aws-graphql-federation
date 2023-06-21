package com.github.arhor.dgs.users.data.converter

import com.github.arhor.dgs.users.data.converter.EnumSetReadingConverterTest.TestEnum.A
import com.github.arhor.dgs.users.data.converter.EnumSetReadingConverterTest.TestEnum.B
import com.github.arhor.dgs.users.data.converter.EnumSetReadingConverterTest.TestEnum.C
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.EnumSet
import java.util.stream.Stream

internal class EnumSetReadingConverterTest {

    enum class TestEnum { A, B, C }

    private val readingConverter = EnumSetReadingConverter(TestEnum::class.java)
    private val writingConverter = EnumSetWritingConverter(TestEnum::class.java)

    @MethodSource
    @ParameterizedTest
    fun `converters should correctly serialize and deserialize input`(
        // Given
        initialData: EnumSet<TestEnum>,
    ) {
        // When
        val resultItems = initialData
            .let(writingConverter::convert)
            .let(readingConverter::convert)

        // Then
        assertThat(resultItems)
            .containsExactlyInAnyOrderElementsOf(initialData)
    }

    companion object {
        @JvmStatic
        fun `converters should correctly serialize and deserialize input`(): Stream<Arguments> =
            Stream.of(
                arguments(EnumSet.noneOf(TestEnum::class.java)),
                arguments(EnumSet.of(A)),
                arguments(EnumSet.of(B)),
                arguments(EnumSet.of(C)),
                arguments(EnumSet.of(A, B)),
                arguments(EnumSet.of(B, C)),
                arguments(EnumSet.of(C, A)),
                arguments(EnumSet.allOf(TestEnum::class.java)),
            )
    }
}
