package com.github.arhor.dgs.users.service

@JvmInline
value class Jwt(val value: String) {

    companion object {
        inline operator fun invoke(source: () -> String) = Jwt(
            value = source()
        )
    }
}
