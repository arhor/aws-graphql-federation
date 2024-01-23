package com.github.arhor.aws.graphql.federation.comments.api.graphql.datafetcher;

import com.netflix.graphql.dgs.DgsQueryExecutor;
import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration;
import com.netflix.graphql.dgs.autoconfig.DgsExtendedScalarsAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
    classes = {
        DgsAutoConfiguration.class,
        DgsExtendedScalarsAutoConfiguration.class,
        FederatedEntityFetcher.class,
    }
)
class FederatedEntityFetcherTest {

    @Autowired
    private DgsQueryExecutor dgsQueryExecutor;

}
