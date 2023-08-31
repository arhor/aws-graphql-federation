package com.github.arhor.dgs.users.data.converter

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.postgresql.util.PGobject
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter

private typealias JSON = Map<String, Any?>

private const val DB_TYPE_NAME = "jsonb"

@WritingConverter
class JsonReadingConverter(private val objectMapper: ObjectMapper) : Converter<JSON, PGobject> {

    override fun convert(source: JSON): PGobject {
        return objectMapper.writeValueAsString(source).let {
            PGobject().apply {
                type = DB_TYPE_NAME
                value = it
            }
        }
    }
}

@ReadingConverter
class JsonWritingConverter(private val objectMapper: ObjectMapper) : Converter<PGobject, JSON> {

    override fun convert(source: PGobject): JSON {
        return source.value?.let { objectMapper.readValue(it) }
            ?: emptyMap()
    }
}
