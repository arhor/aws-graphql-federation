package com.github.arhor.dgs.users.service

import org.slf4j.LoggerFactory

interface UserPasswordEncoder {

    fun encode(password: String): String {
        logger.warn("Default NO-OP implementation is used for UserPasswordEncoder!")
        return password
    }

    companion object {
        private val logger = LoggerFactory.getLogger(UserPasswordEncoder::class.java)
    }
}
