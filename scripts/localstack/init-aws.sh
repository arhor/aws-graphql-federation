#!/bin/bash

user_changes_arn=$(awslocal sns create-topic --name user-changes --output text)
post_changes_arn=$(awslocal sns create-topic --name post-changes --output text)

user_updates_for_posts_url=$(awslocal sqs create-queue --queue-name user-updates-for-posts --output text)
user_deletes_for_posts_url=$(awslocal sqs create-queue --queue-name user-deletes-for-posts --output text)

post_updates_for_comments_url=$(awslocal sqs create-queue --queue-name post-updates-for-comments --output text)
post_deletes_for_comments_url=$(awslocal sqs create-queue --queue-name post-deletes-for-comments --output text)

user_updates_for_posts_arn=$(awslocal sqs get-queue-attributes --queue-url "$user_updates_for_posts_url" --attribute-names QueueArn --query Attributes --output text)
user_deletes_for_posts_arn=$(awslocal sqs get-queue-attributes --queue-url "$user_deletes_for_posts_url" --attribute-names QueueArn --query Attributes --output text)

post_updates_for_comments_arn=$(awslocal sqs get-queue-attributes --queue-url "$post_updates_for_comments_url" --attribute-names QueueArn --query Attributes --output text)
post_deletes_for_comments_arn=$(awslocal sqs get-queue-attributes --queue-url "$post_deletes_for_comments_url" --attribute-names QueueArn --query Attributes --output text)

awslocal sns subscribe \
    --topic-arn "$user_changes_arn" \
    --protocol sqs \
    --notification-endpoint "$user_updates_for_posts_arn" \
    --attributes '{ "FilterPolicy": "{\"x_payload_type\":[\"UserChange.Updated\"]}", "RawMessageDelivery": "true" }'

awslocal sns subscribe \
    --topic-arn "$user_changes_arn" \
    --protocol sqs \
    --notification-endpoint "$user_deletes_for_posts_arn" \
    --attributes '{ "FilterPolicy": "{\"x_payload_type\":[\"UserChange.Deleted\"]}", "RawMessageDelivery": "true" }'

awslocal sns subscribe \
    --topic-arn "$post_changes_arn" \
    --protocol sqs \
    --notification-endpoint "$post_updates_for_comments_arn" \
    --attributes '{ "FilterPolicy": "{\"x_payload_type\":[\"PostChange.Updated\"]}", "RawMessageDelivery": "true" }'

awslocal sns subscribe \
    --topic-arn "$post_changes_arn" \
    --protocol sqs \
    --notification-endpoint "$post_deletes_for_comments_arn" \
    --attributes '{ "FilterPolicy": "{\"x_payload_type\":[\"PostChange.Deleted\"]}", "RawMessageDelivery": "true" }'