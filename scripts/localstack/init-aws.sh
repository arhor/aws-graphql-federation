#!/bin/bash

### User ###

awslocal sns create-topic --name user-state-changes --output table | cat

awslocal sqs create-queue --queue-name user-state-changes-dgs-articles --output table | cat
awslocal sqs create-queue --queue-name user-state-changes-dgs-comments --output table | cat
awslocal sqs create-queue --queue-name user-state-changes-dgs-extradata --output table | cat

awslocal sns subscribe \
    --topic-arn "arn:aws:sns:us-east-1:000000000000:user-state-changes" \
    --protocol sqs \
    --notification-endpoint "arn:aws:sqs:us-east-1:000000000000:user-state-changes-dgs-articles" \
    --output table | cat

awslocal sns subscribe \
    --topic-arn "arn:aws:sns:us-east-1:000000000000:user-state-changes" \
    --protocol sqs \
    --notification-endpoint "arn:aws:sqs:us-east-1:000000000000:user-state-changes-dgs-comments" \
    --output table | cat

awslocal sns subscribe \
    --topic-arn "arn:aws:sns:us-east-1:000000000000:user-state-changes" \
    --protocol sqs \
    --notification-endpoint "arn:aws:sqs:us-east-1:000000000000:user-state-changes-dgs-extradata" \
    --output table | cat
