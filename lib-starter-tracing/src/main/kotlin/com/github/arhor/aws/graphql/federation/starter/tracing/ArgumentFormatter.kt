package com.github.arhor.aws.graphql.federation.starter.tracing

interface ArgumentFormatter {
    val argumentType: Class<*>
    fun format(arg: Any): String

    companion object {
        val SIMPLE = object : ArgumentFormatter {
            override val argumentType: Class<*> = Any::class.java
            override fun format(arg: Any): String = arg.toString()
        }
    }
}
