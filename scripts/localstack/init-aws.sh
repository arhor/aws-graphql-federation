#!/bin/bash

############################################## Event: user updated #####################################################

# supplier: user-service
awslocal sns create-topic --name user-updated-events-topic --output table | cat

# consumer: budget-overrun-tracker
awslocal sqs create-queue --queue-name user-updated-events-queue-1 --output table | cat

# consumer: email-notifier
awslocal sqs create-queue --queue-name user-updated-events-queue-2 --output table | cat

awslocal sns subscribe \
    --topic-arn "arn:aws:sns:us-east-1:000000000000:user-updated-events-topic" \
    --protocol sqs \
    --notification-endpoint "arn:aws:sqs:us-east-1:000000000000:user-updated-events-queue-1" \
    --output table | cat

awslocal sns subscribe \
    --topic-arn "arn:aws:sns:us-east-1:000000000000:user-updated-events-topic" \
    --protocol sqs \
    --notification-endpoint "arn:aws:sqs:us-east-1:000000000000:user-updated-events-queue-2" \
    --output table | cat

############################################## Event: user deleted #####################################################

# supplier: user-service
awslocal sns create-topic --name user-deleted-events-topic --output table | cat

# consumer: budget-overrun-tracker
awslocal sqs create-queue --queue-name user-deleted-events-queue-1 --output table | cat

# consumer: email-notifier
awslocal sqs create-queue --queue-name user-deleted-events-queue-2 --output table | cat

# consumer: expense-service
awslocal sqs create-queue --queue-name user-deleted-events-queue-3 --output table | cat

awslocal sns subscribe \
    --topic-arn "arn:aws:sns:us-east-1:000000000000:user-deleted-events-topic" \
    --protocol sqs \
    --notification-endpoint "arn:aws:sqs:us-east-1:000000000000:user-deleted-events-queue-1" \
    --output table | cat

awslocal sns subscribe \
    --topic-arn "arn:aws:sns:us-east-1:000000000000:user-deleted-events-topic" \
    --protocol sqs \
    --notification-endpoint "arn:aws:sqs:us-east-1:000000000000:user-deleted-events-queue-2" \
    --output table | cat

awslocal sns subscribe \
    --topic-arn "arn:aws:sns:us-east-1:000000000000:user-deleted-events-topic" \
    --protocol sqs \
    --notification-endpoint "arn:aws:sqs:us-east-1:000000000000:user-deleted-events-queue-3" \
    --output table | cat

############################################ Event: expense updated ####################################################

# supplier: expense-service
awslocal sns create-topic --name expense-updated-events-topic --output table | cat

# consumer: budget-overrun-tracker
awslocal sqs create-queue --queue-name expense-updated-events-queue-1 --output table | cat

awslocal sns subscribe \
    --topic-arn "arn:aws:sns:us-east-1:000000000000:expense-updated-events-topic" \
    --protocol sqs \
    --notification-endpoint "arn:aws:sqs:us-east-1:000000000000:expense-updated-events-queue-1" \
    --output table | cat

############################################ Event: expense deleted ####################################################

# supplier: expense-service
awslocal sns create-topic --name expense-deleted-events-topic --output table | cat

# consumer: budget-overrun-tracker
awslocal sqs create-queue --queue-name expense-deleted-events-queue-1 --output table | cat

awslocal sns subscribe \
    --topic-arn "arn:aws:sns:us-east-1:000000000000:expense-deleted-events-topic" \
    --protocol sqs \
    --notification-endpoint "arn:aws:sqs:us-east-1:000000000000:expense-deleted-events-queue-1" \
    --output table | cat

############################################# Event: budget overrun ####################################################

# supplier: budget-overrun-tracker
awslocal sns create-topic --name budget-overrun-events-topic --output table | cat

# consumer: email-notifier
awslocal sqs create-queue --queue-name budget-overrun-events-queue-1 --output table | cat

awslocal sns subscribe \
    --topic-arn "arn:aws:sns:us-east-1:000000000000:budget-overrun-events-topic" \
    --protocol sqs \
    --notification-endpoint "arn:aws:sqs:us-east-1:000000000000:budget-overrun-events-queue-1" \
    --output table | cat
