#!/bin/bash

awslocal cloudformation deploy \
    --stack-name "aws-graphql-federation" \
    --template-file "/.build/aws-application-resources.yml"
