package com.github.arhor.aws.graphql.federation.users.data.converter

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.postgresql.util.PGobject
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter

@ReadingConverter
class JsonReadingConverter(private val objectMapper: ObjectMapper) : Converter<PGobject, Map<String, Any?>> {

    override fun convert(source: PGobject): Map<String, Any?> {
        return source.value?.let { objectMapper.readValue(it) }
            ?: emptyMap()
    }
}
