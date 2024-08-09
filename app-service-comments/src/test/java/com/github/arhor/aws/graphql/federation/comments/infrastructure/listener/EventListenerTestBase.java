package com.github.arhor.aws.graphql.federation.comments.infrastructure.listener;

import com.github.arhor.aws.graphql.federation.starter.testing.ConstantsKt;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import io.awspring.cloud.test.sqs.SqsTest;
import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import static com.github.arhor.aws.graphql.federation.starter.tracing.AttributesKt.IDEMPOTENT_KEY;
import static com.github.arhor.aws.graphql.federation.starter.tracing.AttributesKt.TRACING_ID_KEY;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SQS;

@Tag("integration")
@SqsTest
@DirtiesContext
@Testcontainers(disabledWithoutDocker = true)
abstract class EventListenerTestBase {

    static final UUID USER_ID = ConstantsKt.getZERO_UUID_VAL();
    static final UUID POST_ID = ConstantsKt.getOMNI_UUID_VAL();
    static final UUID TRACE_ID = ConstantsKt.getTEST_1_UUID_VAL();
    static final UUID IDEMPOTENCY_KEY = ConstantsKt.getTEST_2_UUID_VAL();

    static final Map<String, Object> MESSAGE_HEADERS = Map.of(
        TRACING_ID_KEY, TRACE_ID,
        IDEMPOTENT_KEY, IDEMPOTENCY_KEY
    );

    @Container
    private final static LocalStackContainer localStack = new LocalStackContainer(
        DockerImageName.parse("localstack/localstack:3.4")
    );

    @Autowired
    protected SqsTemplate sqsTemplate;

    @DynamicPropertySource
    static void registerDynamicProperties(final DynamicPropertyRegistry registry) {
        registry.add("spring.cloud.aws.credentials.access-key", localStack::getAccessKey);
        registry.add("spring.cloud.aws.credentials.secret-key", localStack::getSecretKey);
        registry.add("spring.cloud.aws.region.static", localStack::getRegion);
        registry.add("spring.cloud.aws.sqs.endpoint", () -> localStack.getEndpointOverride(SQS).toString());
    }

    protected static void createdQueue(final String queueName) throws IOException, InterruptedException {
        localStack.execInContainer("awslocal", "sqs", "create-queue", "--queue-name", queueName);
    }
}
