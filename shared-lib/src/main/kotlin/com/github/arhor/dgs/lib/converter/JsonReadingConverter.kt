package com.github.arhor.dgs.lib.converter

import com.fasterxml.jackson.databind.ObjectMapper
import org.postgresql.util.PGobject
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.WritingConverter

@WritingConverter
class JsonReadingConverter(private val objectMapper: ObjectMapper) : Converter<Map<String, Any>, PGobject> {

    override fun convert(source: Map<String, Any>): PGobject {
        return objectMapper.writeValueAsString(source).let {
            PGobject().apply {
                type = DB_TYPE_NAME
                value = it
            }
        }
    }

    companion object {
        private const val DB_TYPE_NAME = "jsonb"
    }
}
