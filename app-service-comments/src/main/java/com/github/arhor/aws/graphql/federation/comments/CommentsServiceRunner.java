package com.github.arhor.aws.graphql.federation.comments;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@SpringBootApplication
public class CommentsServiceRunner {

    public static void main(final String[] args) {
        SpringApplication.run(CommentsServiceRunner.class, args);
    }
}
