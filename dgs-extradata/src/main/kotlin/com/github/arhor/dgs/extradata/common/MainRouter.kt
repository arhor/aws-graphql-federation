package com.github.arhor.dgs.extradata.common

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.servlet.function.RouterFunction
import org.springframework.web.servlet.function.ServerResponse
import org.springframework.web.servlet.function.router
import java.time.LocalDateTime
import java.util.function.Supplier

@Component
class MainRouter(currentDateTimeSupplier: Supplier<LocalDateTime>) : RouterFunction<ServerResponse> by router({

    GET(pattern = "/api/health") {
        ServerResponse
            .status(HttpStatus.OK)
            .body("UP")
    }

    onError<Throwable> { e, _ ->
        logger.error("An exception occurred.", e)

        ServerResponse
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(
                mapOf(
                    "requestId" to com.github.arhor.dgs.extradata.common.CurrentRequestContext.requestId,
                    "timestamp" to currentDateTimeSupplier.get(),
                    "message" to e.message,
                )
            )
    }
}) {
    companion object {
        private val logger = LoggerFactory.getLogger(MainRouter::class.java)
    }
}
