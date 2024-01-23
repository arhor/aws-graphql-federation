package com.github.arhor.aws.graphql.federation.comments.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration(proxyBeanMethods = false)
public class ConfigureTestObjectMapper {

    @Bean
    public ObjectMapper objectMapper() {
        return JsonMapper.builder().build();
    }
}
