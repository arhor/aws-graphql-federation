package com.github.arhor.aws.graphql.federation.spring.core.data

import com.github.arhor.aws.graphql.federation.spring.core.data.FeaturesConvertersTest.TestFeature.TEST_1
import com.github.arhor.aws.graphql.federation.spring.core.data.FeaturesConvertersTest.TestFeature.TEST_2
import com.github.arhor.aws.graphql.federation.spring.core.data.FeaturesConvertersTest.TestFeature.TEST_3
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.EnumSet
import java.util.stream.Stream

class FeaturesConvertersTest {

    private val featuresWritingConverter = FeaturesWritingConverter(TEST_FEATURE_CLASS)
    private val featuresReadingConverter = FeaturesReadingConverter(TEST_FEATURE_CLASS)

    @MethodSource
    @ParameterizedTest
    fun `converters should correctly serialize and deserialize input`(
        // Given
        initialData: Features<TestFeature>,
    ) {
        // When
        val result = initialData
            .let(featuresWritingConverter::convert)
            .let(featuresReadingConverter::convert)

        // Then
        assertThat(result.items)
            .containsExactlyInAnyOrderElementsOf(initialData.items)
    }

    enum class TestFeature {
        TEST_1,
        TEST_2,
        TEST_3,
    }

    companion object {
        private val TEST_FEATURE_CLASS = TestFeature::class.java

        @JvmStatic
        fun `converters should correctly serialize and deserialize input`(): Stream<Arguments> =
            Stream.of(
                arguments(Features(EnumSet.noneOf(TEST_FEATURE_CLASS))),
                arguments(Features(TEST_1)),
                arguments(Features(TEST_2)),
                arguments(Features(TEST_3)),
                arguments(Features(TEST_1, TEST_2)),
                arguments(Features(TEST_2, TEST_3)),
                arguments(Features(TEST_3, TEST_1)),
                arguments(Features(EnumSet.allOf(TEST_FEATURE_CLASS))),
            )
    }
}
