package com.github.arhor.dgs.comments

import com.fasterxml.jackson.databind.ObjectMapper
import io.awspring.cloud.sqs.annotation.SqsListener
import org.slf4j.LoggerFactory
import org.springframework.messaging.Message
import org.springframework.stereotype.Component
import java.util.function.Consumer

@Component
class UserStateChangeListener(
    private val consumer: Consumer<String>,
    private val objectMapper: ObjectMapper,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @SqsListener("\${app-props.aws.user-updated-queue}")
    fun handleUserUpdate(message: Message<String>) {
        logger.debug("Processing event: {}", message)
        consumer.accept(objectMapper.registeredModuleIds.joinToString())
    }

    @SqsListener("\${app-props.aws.user-deleted-queue}")
    fun handleUserDelete(message: Message<String>) {
        logger.debug("Processing event: {}", message)
        consumer.accept(objectMapper.registeredModuleIds.joinToString())
    }

//    sealed interface UserStateChange {
//        data class Updated @JsonCreator constructor(val userId: String) : UserStateChange
//        data class Deleted @JsonCreator constructor(val userId: String) : UserStateChange
//    }
}
