package com.github.arhor.aws.graphql.federation.spring.core.data

import com.fasterxml.jackson.databind.ObjectMapper
import org.postgresql.util.PGobject
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.WritingConverter

@WritingConverter
class JsonWritingConverter(private val objectMapper: ObjectMapper) : Converter<Map<String, Any?>, PGobject> {

    override fun convert(source: Map<String, Any?>): PGobject {
        return objectMapper.writeValueAsString(source).let {
            PGobject().apply {
                type = "jsonb"
                value = it
            }
        }
    }
}
