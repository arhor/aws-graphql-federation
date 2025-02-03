package com.github.arhor.aws.graphql.federation.starter.core.data;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.util.EnumSet;
import java.util.function.Function;

/**
 * Must be extended to properly reify {@link F} generic type argument.
 * <p>
 * Generics in Java are erased during compilation, the only exception I know - generic type arguments
 * used during inheritance. So, it's impossible to reify generic type argument from the following
 * declaration:
 * <pre>
 * final var list = new ArrayList&lt;Integer&gt;();
 * </pre>
 * <p>
 * But, it's possible if we use inheritance:
 * <pre>
 * class IntegerArrayList extends ArrayList&lt;Integer&gt; {}
 * final var list = new IntegerArrayList();
 * </pre>
 * <p>
 * The same approach works with anonymous classes, since they syntax mixes object instantiation with
 * class declaration.
 */
@ReadingConverter
public abstract class FeaturesReadingConverter<F extends Features<F, E>, E extends Enum<E>>
    implements Converter<Integer, F> {

    private final Class<E> type;
    private final Function<EnumSet<E>, F> factory;

    protected FeaturesReadingConverter(final Class<E> type, final Function<EnumSet<E>, F> factory) {
        this.type = type;
        this.factory = factory;
    }

    @NotNull
    @Override
    @SuppressWarnings("UnnecessaryUnboxing")
    public F convert(@NotNull final Integer source) {
        final var result = EnumSet.noneOf(type);
        final var number = source.intValue();

        if (number != 0) {
            for (final var item : EnumSet.allOf(type)) {
                if ((number & (1 << item.ordinal())) != 0) {
                    result.add(item);
                }
            }
        }
        return factory.apply(result);
    }
}
