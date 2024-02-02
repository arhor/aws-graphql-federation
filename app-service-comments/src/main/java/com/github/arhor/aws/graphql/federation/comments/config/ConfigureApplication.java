package com.github.arhor.aws.graphql.federation.comments.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@Configuration(proxyBeanMethods = false)
public class ConfigureApplication {
}
