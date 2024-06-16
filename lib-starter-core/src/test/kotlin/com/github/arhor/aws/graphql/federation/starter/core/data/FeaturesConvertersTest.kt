package com.github.arhor.aws.graphql.federation.starter.core.data

import com.github.arhor.aws.graphql.federation.starter.core.data.FeaturesConvertersTest.TestFeature.TEST_1
import com.github.arhor.aws.graphql.federation.starter.core.data.FeaturesConvertersTest.TestFeature.TEST_2
import com.github.arhor.aws.graphql.federation.starter.core.data.FeaturesConvertersTest.TestFeature.TEST_3
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.EnumSet
import java.util.stream.Stream

class FeaturesConvertersTest {

    private val featuresWritingConverter = FeaturesWritingConverter()
    private val featuresReadingConverter = FeaturesReadingConverter(TestFeature::class.java, ::TestFeatures)

    @MethodSource
    @ParameterizedTest
    fun `converters should correctly serialize and deserialize input`(
        // Given
        initialData: TestFeatures,
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

    class TestFeatures(items: EnumSet<TestFeature>) : Features<TestFeatures, TestFeature>(items) {
        override fun create(items: EnumSet<TestFeature>) = TestFeatures(items)
    }

    companion object {
        @JvmStatic
        fun `converters should correctly serialize and deserialize input`(): Stream<Arguments> =
            Stream.of(
                arguments(TestFeatures(EnumSet.noneOf(TestFeature::class.java))),
                arguments(TestFeatures(EnumSet.of(TEST_1))),
                arguments(TestFeatures(EnumSet.of(TEST_2))),
                arguments(TestFeatures(EnumSet.of(TEST_3))),
                arguments(TestFeatures(EnumSet.of(TEST_1, TEST_2))),
                arguments(TestFeatures(EnumSet.of(TEST_2, TEST_3))),
                arguments(TestFeatures(EnumSet.of(TEST_3, TEST_1))),
                arguments(TestFeatures(EnumSet.allOf(TestFeature::class.java))),
            )
    }
}
