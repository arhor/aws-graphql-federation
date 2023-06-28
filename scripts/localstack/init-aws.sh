#!/bin/bash

user_state_changes=$(awslocal sns create-topic --name user-state-changes --output text)

user_updated_events_1_url=$(awslocal sqs create-queue --queue-name user-updated-events-1 --output text)
user_deleted_events_1_url=$(awslocal sqs create-queue --queue-name user-deleted-events-1 --output text)

user_updated_events_1_arn=$(awslocal sqs get-queue-attributes --queue-url "$user_updated_events_1_url" --attribute-names QueueArn --query Attributes --output text)
user_deleted_events_1_arn=$(awslocal sqs get-queue-attributes --queue-url "$user_deleted_events_1_url" --attribute-names QueueArn --query Attributes --output text)

awslocal sns subscribe \
    --topic-arn "$user_state_changes" \
    --protocol sqs \
    --notification-endpoint "$user_updated_events_1_arn" \
    --attributes '{ "FilterPolicy": "{\"x_payload_type\":[\"UserStateChange.Updated\"]}" }'

awslocal sns subscribe \
    --topic-arn "$user_state_changes" \
    --protocol sqs \
    --notification-endpoint "$user_deleted_events_1_arn" \
    --attributes '{ "FilterPolicy": "{\"x_payload_type\":[\"UserStateChange.Deleted\"]}" }'
