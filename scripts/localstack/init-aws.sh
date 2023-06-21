#!/bin/bash

### User ###

awslocal sns create-topic --name user-updated-events --output table | cat

awslocal sqs create-queue --queue-name user-updated-events-1 --output table | cat
awslocal sqs create-queue --queue-name user-updated-events-2 --output table | cat

awslocal sns subscribe \
    --topic-arn "arn:aws:sns:us-east-1:000000000000:user-updated-events" \
    --protocol sqs \
    --notification-endpoint "arn:aws:sqs:us-east-1:000000000000:user-updated-events-1" \
    --output table | cat

awslocal sns subscribe \
    --topic-arn "arn:aws:sns:us-east-1:000000000000:user-updated-events" \
    --protocol sqs \
    --notification-endpoint "arn:aws:sqs:us-east-1:000000000000:user-updated-events-1" \
    --output table | cat

awslocal sns create-topic --name user-deleted-events --output table | cat

awslocal sqs create-queue --queue-name user-deleted-events-1 --output table | cat
awslocal sqs create-queue --queue-name user-deleted-events-2 --output table | cat

awslocal sns subscribe \
    --topic-arn "arn:aws:sns:us-east-1:000000000000:user-deleted-events" \
    --protocol sqs \
    --notification-endpoint "arn:aws:sqs:us-east-1:000000000000:user-deleted-events-1" \
    --output table | cat

awslocal sns subscribe \
    --topic-arn "arn:aws:sns:us-east-1:000000000000:user-deleted-events" \
    --protocol sqs \
    --notification-endpoint "arn:aws:sqs:us-east-1:000000000000:user-deleted-events-1" \
    --output table | cat

### Topic ###
awslocal sns create-topic --name topic-deleted-events --output table | cat

awslocal sqs create-queue --queue-name topic-deleted-events-1 --output table | cat

awslocal sns subscribe \
    --topic-arn "arn:aws:sns:us-east-1:000000000000:topic-deleted-events" \
    --protocol sqs \
    --notification-endpoint "arn:aws:sqs:us-east-1:000000000000:topic-deleted-events-1" \
    --output table | cat

### Post ###

awslocal sns create-topic --name post-deleted-events --output table | cat

awslocal sqs create-queue --queue-name post-deleted-events-1 --output table | cat

awslocal sns subscribe \
    --topic-arn "arn:aws:sns:us-east-1:000000000000:post-deleted-events" \
    --protocol sqs \
    --notification-endpoint "arn:aws:sqs:us-east-1:000000000000:post-deleted-events-1" \
    --output table | cat
