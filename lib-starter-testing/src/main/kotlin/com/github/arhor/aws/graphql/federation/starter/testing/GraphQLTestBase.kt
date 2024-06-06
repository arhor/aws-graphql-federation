package com.github.arhor.aws.graphql.federation.starter.testing

import com.github.arhor.aws.graphql.federation.starter.graphql.SubgraphComponentsAutoConfiguration
import com.github.arhor.aws.graphql.federation.starter.security.SubgraphSecurityAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest

import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration
import com.netflix.graphql.dgs.autoconfig.DgsExtendedScalarsAutoConfiguration
import com.netflix.graphql.dgs.autoconfig.DgsExtendedValidationAutoConfiguration

@SpringBootTest(
    classes = [
        DgsAutoConfiguration::class,
        DgsExtendedScalarsAutoConfiguration::class,
        DgsExtendedValidationAutoConfiguration::class,
        SubgraphComponentsAutoConfiguration::class,
        SubgraphSecurityAutoConfiguration::class
    ]
)
abstract class GraphQLTestBase
