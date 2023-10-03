package com.github.arhor.dgs.users.data.converter

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.postgresql.util.PGobject
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter

private const val DB_TYPE_NAME = "jsonb"

@WritingConverter
class JsonWritingConverter(private val objectMapper: ObjectMapper) : Converter<Map<String, Any?>, PGobject> {

    override fun convert(source: Map<String, Any?>): PGobject {
        return objectMapper.writeValueAsString(source).let {
            PGobject().apply {
                type = DB_TYPE_NAME
                value = it
            }
        }
    }
}

@ReadingConverter
class JsonReadingConverter(private val objectMapper: ObjectMapper) : Converter<PGobject, Map<String, Any?>> {

    override fun convert(source: PGobject): Map<String, Any?> {
        return source.value?.let { objectMapper.readValue(it) }
            ?: emptyMap()
    }
}
