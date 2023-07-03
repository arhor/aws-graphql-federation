#!/bin/sh

awslocal sqs create-queue --queue-name user-updated-test-events
awslocal sqs create-queue --queue-name user-deleted-test-events
awslocal sqs create-queue --queue-name post-updated-test-events
awslocal sqs create-queue --queue-name post-deleted-test-events

echo "Initialized."
