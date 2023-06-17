package com.github.arhor.dgs.users.graphql.scalar

import com.github.arhor.dgs.users.data.entity.Setting
import com.netflix.graphql.dgs.DgsScalar
import graphql.language.ArrayValue
import graphql.language.EnumValue
import graphql.language.StringValue
import graphql.language.Value
import graphql.schema.Coercing
import graphql.schema.CoercingParseLiteralException
import graphql.schema.CoercingParseValueException
import graphql.schema.CoercingSerializeException
import java.util.EnumSet

@DgsScalar(name = "Settings")
class SettingsScalar : Coercing<EnumSet<Setting>, Iterable<String>> {

    override fun serialize(dataFetcherResult: Any): Iterable<String> {
        return if (dataFetcherResult is EnumSet<*>) {
            dataFetcherResult.map { it.name }
        } else {
            throw CoercingSerializeException("Not a valid EnumSet<Setting>")
        }
    }

    override fun parseValue(data: Any): EnumSet<Setting> {
        return when (data) {
            is String -> {
                data.split(",").asSettings()
            }

            is Iterable<*> -> {
                data.map { it.toString() }.asSettings()
            }

            else -> {
                throw CoercingParseValueException("Expected Iterable or String, but it was: ${data.javaClass.name}")
            }
        }
    }

    override fun parseLiteral(input: Any): EnumSet<Setting> {
        return when (input) {
            is ArrayValue -> input.values.map { extractStringValue(it) }.asSettings()

            else -> throw CoercingParseLiteralException(
                "Expected AST type 'ArrayValue' but was '${input.javaClass.simpleName}'."
            )
        }
    }

    private fun extractStringValue(input: Value<*>): String {
        return when (input) {
            is StringValue -> {
                input.value
            }

            is EnumValue -> {
                input.name
            }

            else -> {
                throw CoercingParseLiteralException("Expected value to be a String but it was '$input'")
            }
        }
    }

    private fun Iterable<String>.asSettings(): EnumSet<Setting> = this
        .map(String::trim)
        .map(Setting::valueOf)
        .let { settings -> Setting.emptySet().apply { addAll(settings) } }
}
