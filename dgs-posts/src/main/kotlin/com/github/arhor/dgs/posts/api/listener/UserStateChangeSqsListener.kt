//package com.github.arhor.dgs.posts.api.listener
//
//import io.awspring.cloud.sqs.annotation.SqsListener
//import org.slf4j.LoggerFactory
//import org.springframework.messaging.Message
//import org.springframework.stereotype.Component
//
//@Component
//class UserStateChangeSqsListener {
//
//    @SqsListener("\${app-props.aws.sqs.user-updates}")
//    fun handleUserUpdates(message: Message<String>) {
//        logger.debug("Processing update event: {}", message)
//    }
//
//    @SqsListener("\${app-props.aws.sqs.user-deletes}")
//    fun handleUserDeletes(message: Message<String>) {
//        logger.debug("Processing delete event: {}", message)
//    }
//
//    companion object {
//        private val logger = LoggerFactory.getLogger(UserStateChangeSqsListener::class.java)
//    }
//}
