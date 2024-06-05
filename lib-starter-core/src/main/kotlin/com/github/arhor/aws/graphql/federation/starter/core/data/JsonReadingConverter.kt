package com.github.arhor.aws.graphql.federation.starter.core.data

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.postgresql.util.PGobject
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter

@ReadingConverter
class JsonReadingConverter(private val objectMapper: ObjectMapper) : Converter<PGobject, Map<String, Any?>> {

    override fun convert(source: PGobject): Map<String, Any?> {
        return source.value?.let { objectMapper.readValue(it, GenericMapTypeRef) }
            ?: emptyMap()
    }

    private object GenericMapTypeRef : TypeReference<Map<String, Any?>>()
}
