package com.github.arhor.aws.graphql.federation.starter.testing

import com.github.arhor.aws.graphql.federation.starter.graphql.SubgraphComponentsAutoConfiguration
import com.github.arhor.aws.graphql.federation.starter.security.SecurityComponentsAutoConfiguration
import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration
import com.netflix.graphql.dgs.autoconfig.DgsExtendedScalarsAutoConfiguration
import com.netflix.graphql.dgs.autoconfig.DgsExtendedValidationAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(
    classes = [
        DgsAutoConfiguration::class,
        DgsExtendedScalarsAutoConfiguration::class,
        DgsExtendedValidationAutoConfiguration::class,
        SecurityComponentsAutoConfiguration::class,
        SubgraphComponentsAutoConfiguration::class,
    ]
)
abstract class GraphQLTestBase
