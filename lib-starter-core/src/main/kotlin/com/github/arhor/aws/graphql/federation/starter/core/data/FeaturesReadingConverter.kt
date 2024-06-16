package com.github.arhor.aws.graphql.federation.starter.core.data

import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import java.util.EnumSet

/**
 * Must be extended to properly reify [F] generic type argument.
 *
 * Generics in Java are erased during compilation, the only exception I know - generic type arguments
 * used during inheritance. So, it's impossible to reify generic type argument from the following
 * declaration:
 * ```java
 * final var list = new ArrayList<Integer>();
 * ```
 *
 * But, it's possible if we use inheritance:
 * ```java
 * class IntegerArrayList extends ArrayList<Integer> {}
 * final var list = new IntegerArrayList();
 * ```
 *
 * The same approach works with anonymous classes, since they syntax mixes object instantiation with
 * class declaration.
 */
@ReadingConverter
open class FeaturesReadingConverter<F : Features<F, E>, E : Enum<E>>(
    val type: Class<E>,
    val factory: (EnumSet<E>) -> F,
) : Converter<Int, F> {

    override fun convert(source: Int): F =
        factory(
            EnumSet.noneOf(type).apply {
                if (source != 0) {
                    for (item in EnumSet.allOf(type)) {
                        if ((source and (1 shl item.ordinal)) != 0) {
                            add(item)
                        }
                    }
                }
            }
        )
}
