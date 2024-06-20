package com.github.arhor.aws.graphql.federation.starter.core.data;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
public class FeaturesWritingConverter implements Converter<Features<?, ?>, Integer> {

    @NotNull
    @Override
    public Integer convert(@NotNull final Features<?, ?> source) {
        var result = 0;

        for (final var item : source.getItems()) {
            result |= 1 << item.ordinal();
        }
        return result;
    }
}
