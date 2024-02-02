package com.github.arhor.aws.graphql.federation.posts

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PostsServiceRunner

fun main(args: Array<String>) {
    runApplication<PostsServiceRunner>(*args)
}
