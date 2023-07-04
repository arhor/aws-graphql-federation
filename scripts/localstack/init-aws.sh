#!/bin/bash

######################################################## USERS #########################################################

user_events_arn=$(awslocal sns create-topic --name user-events --output text)

user_created_events_for_posts_url=$(awslocal sqs create-queue --queue-name user-created-events-for-posts --output text)
user_deleted_events_for_posts_url=$(awslocal sqs create-queue --queue-name user-deleted-events-for-posts --output text)

user_created_events_for_comments_url=$(awslocal sqs create-queue --queue-name user-created-events-for-comments --output text)
user_deleted_events_for_comments_url=$(awslocal sqs create-queue --queue-name user-deletes-events-for-comments --output text)

user_created_events_for_posts_arn=$(awslocal sqs get-queue-attributes --queue-url "$user_created_events_for_posts_url" --attribute-names QueueArn --query Attributes --output text)
user_deleted_events_for_posts_arn=$(awslocal sqs get-queue-attributes --queue-url "$user_deleted_events_for_posts_url" --attribute-names QueueArn --query Attributes --output text)

user_created_events_for_comments_arn=$(awslocal sqs get-queue-attributes --queue-url "$user_created_events_for_comments_url" --attribute-names QueueArn --query Attributes --output text)
user_deleted_events_for_comments_arn=$(awslocal sqs get-queue-attributes --queue-url "$user_deleted_events_for_comments_url" --attribute-names QueueArn --query Attributes --output text)

awslocal sns subscribe \
    --topic-arn "$user_events_arn" \
    --protocol sqs \
    --notification-endpoint "$user_created_events_for_posts_arn" \
    --attributes '{ "FilterPolicy": "{\"x_event_type\":[\"UserEvent.Created\"]}", "RawMessageDelivery": "true" }'

awslocal sns subscribe \
    --topic-arn "$user_events_arn" \
    --protocol sqs \
    --notification-endpoint "$user_deleted_events_for_posts_arn" \
    --attributes '{ "FilterPolicy": "{\"x_event_type\":[\"UserEvent.Deleted\"]}", "RawMessageDelivery": "true" }'

awslocal sns subscribe \
    --topic-arn "$user_events_arn" \
    --protocol sqs \
    --notification-endpoint "$user_created_events_for_comments_arn" \
    --attributes '{ "FilterPolicy": "{\"x_event_type\":[\"UserEvent.Created\"]}", "RawMessageDelivery": "true" }'

awslocal sns subscribe \
    --topic-arn "$user_events_arn" \
    --protocol sqs \
    --notification-endpoint "$user_deleted_events_for_comments_arn" \
    --attributes '{ "FilterPolicy": "{\"x_event_type\":[\"UserEvent.Deleted\"]}", "RawMessageDelivery": "true" }'

######################################################## POSTS #########################################################

post_changes_arn=$(awslocal sns create-topic --name post-changes --output text)

post_created_events_for_comments_url=$(awslocal sqs create-queue --queue-name post-created-events-for-comments --output text)
post_deleted_events_for_comments_url=$(awslocal sqs create-queue --queue-name post-deleted-events-for-comments --output text)

post_created_events_for_comments_arn=$(awslocal sqs get-queue-attributes --queue-url "$post_created_events_for_comments_url" --attribute-names QueueArn --query Attributes --output text)
post_deleted_events_for_comments_arn=$(awslocal sqs get-queue-attributes --queue-url "$post_deleted_events_for_comments_url" --attribute-names QueueArn --query Attributes --output text)

awslocal sns subscribe \
    --topic-arn "$post_changes_arn" \
    --protocol sqs \
    --notification-endpoint "$post_created_events_for_comments_arn" \
    --attributes '{ "FilterPolicy": "{\"x_event_type\":[\"PostEvent.Created\"]}", "RawMessageDelivery": "true" }'

awslocal sns subscribe \
    --topic-arn "$post_changes_arn" \
    --protocol sqs \
    --notification-endpoint "$post_deleted_events_for_comments_arn" \
    --attributes '{ "FilterPolicy": "{\"x_event_type\":[\"PostEvent.Deleted\"]}", "RawMessageDelivery": "true" }'
